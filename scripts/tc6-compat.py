#!/usr/bin/env python3
"""Deterministic classfile ABI and addon-reference tooling for TC6 compatibility."""

from __future__ import print_function

import argparse
import fnmatch
import hashlib
import io
import json
import os
import re
import struct
import sys
import zipfile


ACC_PUBLIC = 0x0001
ACC_STATIC = 0x0008
ACC_FINAL = 0x0010
ACC_INTERFACE = 0x0200
ACC_ABSTRACT = 0x0400
ACC_PROTECTED = 0x0004
ABI_ACCESS = ACC_PUBLIC | ACC_PROTECTED

THAUMCRAFT_PREFIX = "thaumcraft/"
CLASS_NAME_RE = re.compile(r"^thaumcraft(?:[./][A-Za-z_$][A-Za-z0-9_$]*)+$")
SEMANTIC_LEVELS = ("EXACT", "PROJECTED", "LINK_ONLY", "UNSUPPORTED")
PLATFORM_METHODS = {
    ("java/lang/Enum", "ordinal", "()I"): ACC_PUBLIC | ACC_FINAL,
}

MEMBER_OPCODES = {
    178: "GETSTATIC",
    179: "PUTSTATIC",
    180: "GETFIELD",
    181: "PUTFIELD",
    182: "INVOKEVIRTUAL",
    183: "INVOKESPECIAL",
    184: "INVOKESTATIC",
    185: "INVOKEINTERFACE",
}

CP_CLASS_OPCODES = {18, 19, 20, 187, 189, 192, 193, 197}


class ClassFormatError(ValueError):
    pass


class Reader(object):
    def __init__(self, data):
        self.data = data
        self.pos = 0

    def read(self, length):
        end = self.pos + length
        if end > len(self.data):
            raise ClassFormatError("unexpected end of classfile")
        value = self.data[self.pos:end]
        self.pos = end
        return value

    def u1(self):
        return struct.unpack(">B", self.read(1))[0]

    def u2(self):
        return struct.unpack(">H", self.read(2))[0]

    def u4(self):
        return struct.unpack(">I", self.read(4))[0]


class ClassFile(object):
    def __init__(self, data, source="<bytes>"):
        self.source = source
        reader = Reader(data)
        if reader.u4() != 0xCAFEBABE:
            raise ClassFormatError("{} is not a classfile".format(source))
        self.minor = reader.u2()
        self.major = reader.u2()
        self.constant_pool = self._read_constant_pool(reader)
        self.access = reader.u2()
        self.name = self.class_name(reader.u2())
        super_index = reader.u2()
        self.super_name = self.class_name(super_index) if super_index else "-"
        self.interfaces = [self.class_name(reader.u2()) for _ in range(reader.u2())]
        self.fields = self._read_members(reader)
        self.methods = self._read_members(reader)
        self.attributes = self._read_attributes(reader)
        if reader.pos != len(data):
            raise ClassFormatError("{} has trailing classfile data".format(source))

    def _read_constant_pool(self, reader):
        count = reader.u2()
        pool = [None] * count
        index = 1
        while index < count:
            tag = reader.u1()
            if tag == 1:
                length = reader.u2()
                pool[index] = (tag, reader.read(length).decode("utf-8", "replace"))
            elif tag in (3, 4):
                pool[index] = (tag, reader.read(4))
            elif tag in (5, 6):
                pool[index] = (tag, reader.read(8))
                index += 1
            elif tag in (7, 8, 16, 19, 20):
                pool[index] = (tag, reader.u2())
            elif tag in (9, 10, 11, 12, 17, 18):
                pool[index] = (tag, reader.u2(), reader.u2())
            elif tag == 15:
                pool[index] = (tag, reader.u1(), reader.u2())
            else:
                raise ClassFormatError("{} has unsupported constant-pool tag {}".format(self.source, tag))
            index += 1
        return pool

    def utf8(self, index):
        entry = self.constant_pool[index]
        if not entry or entry[0] != 1:
            raise ClassFormatError("{} has invalid UTF8 index {}".format(self.source, index))
        return entry[1]

    def class_name(self, index):
        entry = self.constant_pool[index]
        if not entry or entry[0] != 7:
            raise ClassFormatError("{} has invalid class index {}".format(self.source, index))
        return self.utf8(entry[1])

    def _read_members(self, reader):
        members = []
        for _ in range(reader.u2()):
            access = reader.u2()
            name = self.utf8(reader.u2())
            descriptor = self.utf8(reader.u2())
            attributes = self._read_attributes(reader)
            members.append((access, name, descriptor, attributes))
        return members

    def _read_attributes(self, reader):
        attributes = []
        for _ in range(reader.u2()):
            name = self.utf8(reader.u2())
            attributes.append((name, reader.read(reader.u4())))
        return attributes

    def name_and_type(self, index):
        entry = self.constant_pool[index]
        if not entry or entry[0] != 12:
            raise ClassFormatError("{} has invalid name-and-type index {}".format(self.source, index))
        return self.utf8(entry[1]), self.utf8(entry[2])

    def member_reference(self, index):
        entry = self.constant_pool[index]
        if not entry or entry[0] not in (9, 10, 11):
            raise ClassFormatError("{} has invalid member-reference index {}".format(self.source, index))
        name, descriptor = self.name_and_type(entry[2])
        return entry[0], self.class_name(entry[1]), name, descriptor


def sha256_file(path):
    digest = hashlib.sha256()
    with open(path, "rb") as handle:
        for chunk in iter(lambda: handle.read(1024 * 1024), b""):
            digest.update(chunk)
    return digest.hexdigest()


def access_text(access, kind):
    common = (
        (0x0001, "public"),
        (0x0002, "private"),
        (0x0004, "protected"),
        (0x0008, "static"),
        (0x0010, "final"),
        (0x1000, "synthetic"),
    )
    class_flags = ((0x0200, "interface"), (0x0400, "abstract"), (0x2000, "annotation"), (0x4000, "enum"))
    method_flags = (
        (0x0020, "synchronized"),
        (0x0040, "bridge"),
        (0x0080, "varargs"),
        (0x0100, "native"),
        (0x0400, "abstract"),
        (0x0800, "strict"),
    )
    field_flags = ((0x0040, "volatile"), (0x0080, "transient"), (0x4000, "enum"))
    flags = list(common)
    if kind == "class":
        flags.extend(class_flags)
    elif kind == "method":
        flags.extend(method_flags)
    elif kind == "field":
        flags.extend(field_flags)
    return ",".join(name for bit, name in flags if access & bit) or "package"


def iter_jar_classes(path, prefix=""):
    with zipfile.ZipFile(path) as jar:
        for name in sorted(jar.namelist()):
            if name.startswith(prefix) and name.endswith(".class"):
                yield ClassFile(jar.read(name), "{}!{}".format(path, name))


def iter_path_classes(path):
    if os.path.isdir(path):
        for root, dirs, files in os.walk(path):
            dirs.sort()
            for name in sorted(files):
                if name.endswith(".class"):
                    full_path = os.path.join(root, name)
                    with open(full_path, "rb") as handle:
                        yield ClassFile(handle.read(), full_path)
        return
    for cls in iter_jar_classes(path):
        yield cls


def descriptor_classes(descriptor):
    return set(re.findall(r"L([^;]+);", descriptor))


def annotation_value(cls, reader):
    tag = chr(reader.u1())
    if tag in "BCDFIJSZs":
        index = reader.u2()
        entry = cls.constant_pool[index]
        return entry[1] if entry and entry[0] == 1 else index
    if tag == "e":
        return (cls.utf8(reader.u2()), cls.utf8(reader.u2()))
    if tag == "c":
        return cls.utf8(reader.u2())
    if tag == "@":
        return read_annotation(cls, reader)
    if tag == "[":
        return [annotation_value(cls, reader) for _ in range(reader.u2())]
    raise ClassFormatError("{} has unsupported annotation value tag {}".format(cls.source, tag))


def read_annotation(cls, reader):
    descriptor = cls.utf8(reader.u2())
    values = {}
    for _ in range(reader.u2()):
        name = cls.utf8(reader.u2())
        values[name] = annotation_value(cls, reader)
    return descriptor, values


def annotations(cls, attributes):
    found = []
    for name, data in attributes:
        if name not in ("RuntimeVisibleAnnotations", "RuntimeInvisibleAnnotations"):
            continue
        reader = Reader(data)
        found.extend(read_annotation(cls, reader) for _ in range(reader.u2()))
        if reader.pos != len(data):
            raise ClassFormatError("{} has trailing annotation data".format(cls.source))
    return found


def code_bytes(cls, attributes):
    for name, data in attributes:
        if name != "Code":
            continue
        reader = Reader(data)
        reader.u2()
        reader.u2()
        yield reader.read(reader.u4())


def instruction_cp_references(code):
    one_byte = set([16, 18, 21, 22, 23, 24, 25, 54, 55, 56, 57, 58, 169, 188])
    two_byte = set([17, 19, 20, 132] + list(range(153, 169)) + list(range(178, 185)) + [187, 189, 192, 193, 198, 199])
    four_byte = set([185, 186, 200, 201])
    pos = 0
    while pos < len(code):
        offset = pos
        opcode = code[pos]
        pos += 1
        if opcode == 170:
            pos += (-pos) % 4
            if pos + 12 > len(code):
                raise ClassFormatError("truncated tableswitch")
            low, high = struct.unpack(">ii", code[pos + 4:pos + 12])
            pos += 12 + 4 * (high - low + 1)
        elif opcode == 171:
            pos += (-pos) % 4
            if pos + 8 > len(code):
                raise ClassFormatError("truncated lookupswitch")
            pairs = struct.unpack(">i", code[pos + 4:pos + 8])[0]
            pos += 8 + 8 * pairs
        elif opcode == 196:
            if pos >= len(code):
                raise ClassFormatError("truncated wide instruction")
            widened = code[pos]
            pos += 5 if widened == 132 else 3
        elif opcode in one_byte:
            if opcode == 18:
                yield offset, opcode, code[pos]
            pos += 1
        elif opcode in two_byte:
            if opcode in MEMBER_OPCODES or opcode in CP_CLASS_OPCODES:
                yield offset, opcode, struct.unpack(">H", code[pos:pos + 2])[0]
            pos += 2
        elif opcode == 197:
            yield offset, opcode, struct.unpack(">H", code[pos:pos + 2])[0]
            pos += 3
        elif opcode in four_byte:
            if opcode == 185:
                yield offset, opcode, struct.unpack(">H", code[pos:pos + 2])[0]
            pos += 4
        if pos > len(code):
            raise ClassFormatError("truncated bytecode instruction at {}".format(offset))


def normalized_class_name(value):
    if value.startswith("L") and value.endswith(";"):
        value = value[1:-1]
    return value.replace(".", "/")


def addon_demands(path):
    classes = set()
    fields = set()
    methods = set()
    mixins = set()
    reflective = set()
    for cls in iter_jar_classes(path):
        mixin_owners = set()
        for descriptor, values in annotations(cls, cls.attributes):
            if descriptor.endswith("/Mixin;"):
                for value in values.get("value", []):
                    owner = normalized_class_name(value)
                    if owner.startswith(THAUMCRAFT_PREFIX):
                        mixin_owners.add(owner)
                for value in values.get("targets", []):
                    owner = normalized_class_name(value)
                    if owner.startswith(THAUMCRAFT_PREFIX):
                        mixin_owners.add(owner)
        for owner in mixin_owners:
            classes.add(owner)

        for entry in cls.constant_pool:
            if not entry:
                continue
            if entry[0] == 7:
                name = cls.utf8(entry[1])
                if name.startswith(THAUMCRAFT_PREFIX):
                    classes.add(name)
                classes.update(name for name in descriptor_classes(name) if name.startswith(THAUMCRAFT_PREFIX))
            elif entry[0] == 8:
                value = cls.utf8(entry[1])
                if CLASS_NAME_RE.match(value) and value.rsplit("/", 1)[-1].rsplit(".", 1)[-1][:1].isupper():
                    reflective.add(normalized_class_name(value))
            elif entry[0] in (12, 16):
                descriptor = cls.utf8(entry[2] if entry[0] == 12 else entry[1])
                classes.update(name for name in descriptor_classes(descriptor) if name.startswith(THAUMCRAFT_PREFIX))

        for _, _, descriptor, _ in cls.fields + cls.methods:
            classes.update(name for name in descriptor_classes(descriptor) if name.startswith(THAUMCRAFT_PREFIX))

        for _, name, descriptor, attributes in cls.methods:
            method_annotations = annotations(cls, attributes)
            if mixin_owners and any(value[0].endswith("/Overwrite;") for value in method_annotations):
                for owner in mixin_owners:
                    mixins.add((owner, name, descriptor, "OVERWRITE"))
            for code in code_bytes(cls, attributes):
                for _, opcode, index in instruction_cp_references(code):
                    if opcode in MEMBER_OPCODES:
                        tag, owner, member_name, member_descriptor = cls.member_reference(index)
                        if owner.startswith(THAUMCRAFT_PREFIX):
                            classes.add(owner)
                            reference = (MEMBER_OPCODES[opcode], owner, member_name, member_descriptor, tag)
                            if tag == 9:
                                fields.add(reference)
                            else:
                                methods.add(reference)
                        classes.update(
                            name for name in descriptor_classes(member_descriptor) if name.startswith(THAUMCRAFT_PREFIX)
                        )
                    elif opcode in CP_CLASS_OPCODES:
                        entry = cls.constant_pool[index]
                        if entry and entry[0] == 7:
                            owner = cls.class_name(index)
                            if owner.startswith(THAUMCRAFT_PREFIX):
                                classes.add(owner)

        for entry in cls.constant_pool:
            if not entry or entry[0] != 15:
                continue
            referenced = cls.constant_pool[entry[2]]
            if referenced and referenced[0] in (9, 10, 11):
                tag, owner, member_name, member_descriptor = cls.member_reference(entry[2])
                if owner.startswith(THAUMCRAFT_PREFIX):
                    classes.add(owner)
                    opcode = "HANDLE{}".format(entry[1])
                    reference = (opcode, owner, member_name, member_descriptor, tag)
                    (fields if tag == 9 else methods).add(reference)

    return {
        "classes": classes,
        "fields": fields,
        "methods": methods,
        "mixins": mixins,
        "reflective": reflective,
    }


def member_demands(path, prefix):
    classes = set()
    fields = set()
    methods = set()
    for cls in iter_jar_classes(path):
        for _, _, _, attributes in cls.methods:
            for code in code_bytes(cls, attributes):
                for _, opcode, index in instruction_cp_references(code):
                    if opcode not in MEMBER_OPCODES:
                        continue
                    tag, owner, name, descriptor = cls.member_reference(index)
                    if not owner.startswith(prefix):
                        continue
                    classes.add(owner)
                    reference = (MEMBER_OPCODES[opcode], owner, name, descriptor, tag)
                    (fields if tag == 9 else methods).add(reference)
    return {
        "classes": classes,
        "fields": fields,
        "methods": methods,
        "mixins": set(),
        "reflective": set(),
    }


def compare_remapped_jars(source, remapped):
    failures = []
    with zipfile.ZipFile(source) as source_jar, zipfile.ZipFile(remapped) as remapped_jar:
        source_names = sorted(source_jar.namelist())
        remapped_names = sorted(remapped_jar.namelist())
        if source_names != remapped_names:
            failures.append("jar entry set changed")
        for name in sorted(set(source_names).intersection(remapped_names)):
            if name.endswith(".class") or name.endswith("/"):
                continue
            if source_jar.read(name) != remapped_jar.read(name):
                failures.append("resource changed: {}".format(name))
                if len(failures) == 20:
                    break
    return failures


def srg_member_names(path):
    fields = set()
    methods = set()
    with io.open(path, "r", encoding="utf-8") as handle:
        for raw in handle:
            parts = raw.split()
            if parts and parts[0] == "FD:" and len(parts) >= 3:
                mcp_name = parts[1].rsplit("/", 1)[-1]
                srg_name = parts[2].rsplit("/", 1)[-1]
                if mcp_name != srg_name:
                    fields.add(srg_name)
            elif parts and parts[0] == "MD:" and len(parts) >= 5:
                mcp_name = parts[1].rsplit("/", 1)[-1]
                srg_name = parts[3].rsplit("/", 1)[-1]
                if mcp_name != srg_name:
                    methods.add((srg_name, parts[4]))
    return fields, methods


def abi_lines(path, prefix="thaumcraft/api/"):
    classes = list(iter_jar_classes(path, prefix))
    major_versions = sorted(set(cls.major for cls in classes))
    lines = [
        "FORMAT tc6-abi-v1",
        "SHA256 {}".format(sha256_file(path)),
        "CLASSFILE_MAJOR {}".format(",".join(str(value) for value in major_versions)),
    ]
    for cls in classes:
        lines.append(
            "C {} {} {} {}".format(
                cls.name,
                access_text(cls.access, "class"),
                cls.super_name,
                ",".join(cls.interfaces) or "-",
            )
        )
        for access, name, descriptor, _ in cls.fields:
            if access & ABI_ACCESS:
                lines.append("F {} {} {} {}".format(cls.name, name, descriptor, access_text(access, "field")))
        for access, name, descriptor, _ in cls.methods:
            if access & ABI_ACCESS:
                lines.append("M {} {} {} {}".format(cls.name, name, descriptor, access_text(access, "method")))
    return lines


def write_lines(path, lines):
    content = "\n".join(lines) + "\n"
    if path == "-":
        sys.stdout.write(content)
        return
    parent = os.path.dirname(path)
    if parent:
        os.makedirs(parent, exist_ok=True)
    with io.open(path, "w", encoding="utf-8", newline="\n") as handle:
        handle.write(content)


def check_snapshot(path, expected_lines):
    with io.open(path, "r", encoding="utf-8") as handle:
        actual_lines = handle.read().splitlines()
    if actual_lines == expected_lines:
        return []
    limit = max(len(actual_lines), len(expected_lines))
    differences = []
    for index in range(limit):
        actual = actual_lines[index] if index < len(actual_lines) else "<missing>"
        expected = expected_lines[index] if index < len(expected_lines) else "<missing>"
        if actual != expected:
            differences.append("line {}: snapshot={!r}; generated={!r}".format(index + 1, actual, expected))
            if len(differences) == 20:
                break
    return differences


def access_satisfies(expected, actual):
    if expected & ACC_PUBLIC:
        return bool(actual & ACC_PUBLIC)
    return bool(actual & ABI_ACCESS)


def is_subtype(index, name, expected, visited=None):
    if name == expected:
        return True
    cls = index.classes.get(name)
    if not cls:
        return False
    visited = set() if visited is None else visited
    if name in visited:
        return False
    visited.add(name)
    parents = cls.interfaces + ([cls.super_name] if cls.super_name != "-" else [])
    return any(is_subtype(index, parent, expected, visited) for parent in parents)


def abi_gap_records(donor_index, target_index, prefix="thaumcraft/api/"):
    gaps = []
    for name in sorted(donor_index.classes):
        donor = donor_index.classes[name]
        if not name.startswith(prefix) or not donor.access & ACC_PUBLIC:
            continue
        target = target_index.classes.get(name)
        symbol = "C {}".format(name)
        if not target:
            gaps.append("CLASS_MISSING " + symbol)
            continue
        if not target.access & ACC_PUBLIC:
            gaps.append("CLASS_ACCESS " + symbol)
        if bool(donor.access & ACC_INTERFACE) != bool(target.access & ACC_INTERFACE):
            gaps.append("CLASS_KIND " + symbol)
        if not donor.access & ACC_FINAL and target.access & ACC_FINAL:
            gaps.append("CLASS_FINAL " + symbol)
        if not donor.access & ACC_ABSTRACT and target.access & ACC_ABSTRACT:
            gaps.append("CLASS_ABSTRACT " + symbol)
        if donor.super_name != "-" and not is_subtype(target_index, name, donor.super_name):
            gaps.append("SUPERCLASS {} {}".format(symbol, donor.super_name))
        for interface in donor.interfaces:
            if not is_subtype(target_index, name, interface):
                gaps.append("INTERFACE {} {}".format(symbol, interface))

        for access, member_name, descriptor, _ in donor.fields:
            if not access & ABI_ACCESS:
                continue
            member = "F {} {} {}".format(name, member_name, descriptor)
            found = target_index.resolve_field(name, member_name, descriptor)
            if not found:
                gaps.append("FIELD_MISSING " + member)
                continue
            _, target_access = found
            if not access_satisfies(access, target_access):
                gaps.append("FIELD_ACCESS " + member)
            if bool(access & ACC_STATIC) != bool(target_access & ACC_STATIC):
                gaps.append("FIELD_STATIC " + member)
            if not access & ACC_FINAL and target_access & ACC_FINAL:
                gaps.append("FIELD_FINAL " + member)

        for access, member_name, descriptor, _ in donor.methods:
            if not access & ABI_ACCESS:
                continue
            member = "M {} {} {}".format(name, member_name, descriptor)
            found = target_index.resolve_method(name, member_name, descriptor)
            if not found:
                gaps.append("METHOD_MISSING " + member)
                continue
            _, target_access = found
            if not access_satisfies(access, target_access):
                gaps.append("METHOD_ACCESS " + member)
            if bool(access & ACC_STATIC) != bool(target_access & ACC_STATIC):
                gaps.append("METHOD_STATIC " + member)
            if not access & ACC_FINAL and target_access & ACC_FINAL and member_name != "<init>":
                gaps.append("METHOD_FINAL " + member)
            if not access & ACC_ABSTRACT and target_access & ACC_ABSTRACT:
                gaps.append("METHOD_ABSTRACT " + member)
    return sorted(gaps)


def abi_gap_lines(donor_path, target_path, prefix="thaumcraft/api/"):
    donor_index = ClassIndex([donor_path])
    target_index = ClassIndex([target_path])
    return [
        "FORMAT tc6-abi-gaps-v1",
        "DONOR_SHA256 {}".format(sha256_file(donor_path)),
    ] + ["G " + gap for gap in abi_gap_records(donor_index, target_index, prefix)]


def read_corpus_manifest(path):
    entries = []
    with io.open(path, "r", encoding="utf-8") as handle:
        for line_number, raw in enumerate(handle, 1):
            line = raw.strip()
            if not line or line.startswith("#"):
                continue
            parts = [part.strip() for part in line.split("|")]
            if len(parts) != 6:
                raise ValueError("{}:{} expected 6 pipe-delimited fields".format(path, line_number))
            addon_id, status, jar, expected_sha, side, source = parts
            if status not in ("supported", "candidate"):
                raise ValueError("{}:{} invalid status {}".format(path, line_number, status))
            if side not in ("server", "client", "both"):
                raise ValueError("{}:{} invalid side {}".format(path, line_number, side))
            entries.append(
                {
                    "id": addon_id,
                    "status": status,
                    "jar": jar,
                    "sha256": expected_sha,
                    "side": side,
                    "source": source,
                }
            )
    return entries


def demand_lines(manifest_path, jar_dir):
    lines = ["FORMAT tc6-addon-demand-v1"]
    entries = read_corpus_manifest(manifest_path)
    all_demands = {}
    for entry in entries:
        jar_path = os.path.join(jar_dir, entry["jar"])
        if not os.path.isfile(jar_path):
            raise IOError("missing corpus jar: {}".format(jar_path))
        actual_sha = sha256_file(jar_path)
        if actual_sha != entry["sha256"]:
            raise ValueError(
                "sha256 mismatch for {}: expected {}, got {}".format(entry["jar"], entry["sha256"], actual_sha)
            )
        demands = addon_demands(jar_path)
        all_demands[entry["id"]] = demands
        lines.append(
            "A {} {} {} {} {}".format(
                entry["id"], entry["status"], entry["side"], entry["jar"], entry["sha256"]
            )
        )
        lines.extend("C {} {}".format(entry["id"], name) for name in sorted(demands["classes"]))
        lines.extend(
            "F {} {} {} {} {}".format(entry["id"], opcode, owner, name, descriptor)
            for opcode, owner, name, descriptor, _ in sorted(demands["fields"])
        )
        lines.extend(
            "M {} {} {} {} {}".format(entry["id"], opcode, owner, name, descriptor)
            for opcode, owner, name, descriptor, _ in sorted(demands["methods"])
        )
        lines.extend(
            "X {} {} {} {} {}".format(entry["id"], kind, owner, name, descriptor)
            for owner, name, descriptor, kind in sorted(demands["mixins"])
        )
        lines.extend("S {} {}".format(entry["id"], name) for name in sorted(demands["reflective"]))
    return entries, all_demands, lines


def read_semantic_policy(path):
    rules = []
    with io.open(path, "r", encoding="utf-8") as handle:
        for line_number, raw in enumerate(handle, 1):
            line = raw.strip()
            if not line or line.startswith("#"):
                continue
            parts = [part.strip() for part in line.split("|")]
            if len(parts) != 3:
                raise ValueError("{}:{} expected level | symbol glob | rationale".format(path, line_number))
            level, pattern, rationale = parts
            if level not in SEMANTIC_LEVELS:
                raise ValueError("{}:{} invalid semantic level {}".format(path, line_number, level))
            if not pattern or not rationale:
                raise ValueError("{}:{} policy pattern and rationale are required".format(path, line_number))
            rules.append((level, pattern, rationale))
    return rules


def semantic_target_lines(demand_path, policy_path):
    symbols = {}
    with io.open(demand_path, "r", encoding="utf-8") as handle:
        for line_number, raw in enumerate(handle, 1):
            parts = raw.strip().split()
            if not parts or parts[0] in ("FORMAT", "A"):
                continue
            if parts[0] not in ("C", "F", "M", "X", "S") or len(parts) < 3:
                raise ValueError("{}:{} invalid demand record".format(demand_path, line_number))
            symbol = " ".join([parts[0]] + parts[2:])
            symbols.setdefault(symbol, set()).add(parts[1])

    rules = read_semantic_policy(policy_path)
    lines = ["FORMAT tc6-target-v1"]
    counts = dict((level, 0) for level in SEMANTIC_LEVELS)
    for symbol in sorted(symbols):
        classification = None
        for level, pattern, rationale in rules:
            if fnmatch.fnmatchcase(symbol, pattern):
                classification = (level, rationale)
                break
        if classification is None:
            raise ValueError("unclassified TC6 demand: {}".format(symbol))
        level, rationale = classification
        counts[level] += 1
        lines.append(
            "K {} {} | {} | {}".format(level, symbol, ",".join(sorted(symbols[symbol])), rationale)
        )
    return lines, counts


class ClassIndex(object):
    def __init__(self, paths, mappings=None):
        self.classes = {}
        self.field_aliases = {}
        self.method_aliases = {}
        for path in paths:
            for cls in iter_path_classes(path):
                if cls.name not in self.classes:
                    self.classes[cls.name] = cls
        if mappings:
            self._load_mappings(mappings)

    def _load_mappings(self, path):
        with io.open(path, "r", encoding="utf-8") as handle:
            for raw in handle:
                parts = raw.split()
                if not parts:
                    continue
                if parts[0] == "FD:" and len(parts) >= 3:
                    mcp_owner, mcp_name = parts[1].rsplit("/", 1)
                    srg_owner, srg_name = parts[2].rsplit("/", 1)
                    cls = self.classes.get(mcp_owner)
                    if cls:
                        for access, name, descriptor, _ in cls.fields:
                            if name == mcp_name:
                                self.field_aliases[(srg_owner, srg_name, descriptor)] = (cls, access)
                elif parts[0] == "MD:" and len(parts) >= 5:
                    mcp_owner, mcp_name = parts[1].rsplit("/", 1)
                    mcp_descriptor = parts[2]
                    srg_owner, srg_name = parts[3].rsplit("/", 1)
                    srg_descriptor = parts[4]
                    cls = self.classes.get(mcp_owner)
                    if cls:
                        for access, name, descriptor, _ in cls.methods:
                            if name == mcp_name and descriptor == mcp_descriptor:
                                self.method_aliases[(srg_owner, srg_name, srg_descriptor)] = (cls, access)

    def resolve_field(self, owner, name, descriptor, visited=None):
        direct_alias = self.field_aliases.get((owner, name, descriptor))
        if direct_alias:
            return direct_alias
        cls = self.classes.get(owner)
        if not cls:
            return None
        visited = set() if visited is None else visited
        if owner in visited:
            return None
        visited.add(owner)
        for access, candidate, candidate_descriptor, _ in cls.fields:
            if candidate == name and candidate_descriptor == descriptor:
                return cls, access
        for parent in cls.interfaces + ([cls.super_name] if cls.super_name != "-" else []):
            found = self.resolve_field(parent, name, descriptor, visited)
            if found:
                return found
        return None

    def resolve_method(self, owner, name, descriptor, visited=None):
        direct_alias = self.method_aliases.get((owner, name, descriptor))
        if direct_alias:
            return direct_alias
        platform_access = PLATFORM_METHODS.get((owner, name, descriptor))
        if platform_access is not None:
            return None, platform_access
        cls = self.classes.get(owner)
        if not cls:
            return None
        visited = set() if visited is None else visited
        if owner in visited:
            return None
        visited.add(owner)
        for access, candidate, candidate_descriptor, _ in cls.methods:
            if candidate == name and candidate_descriptor == descriptor:
                return cls, access
        if name == "<init>":
            return None
        parents = ([cls.super_name] if cls.super_name != "-" else []) + cls.interfaces
        for parent in parents:
            found = self.resolve_method(parent, name, descriptor, visited)
            if found:
                return found
        return None


def resolve_demands(entries, all_demands, index):
    results = []
    statuses = dict((entry["id"], entry["status"]) for entry in entries)
    for addon_id in sorted(all_demands):
        demands = all_demands[addon_id]
        for owner in sorted(demands["classes"]):
            cls = index.classes.get(owner)
            if not cls:
                results.append((addon_id, statuses[addon_id], "MISSING_CLASS", owner))
            elif not cls.access & ACC_PUBLIC:
                results.append((addon_id, statuses[addon_id], "CLASS_ACCESS", owner))
        for opcode, owner, name, descriptor, tag in sorted(demands["fields"]):
            found = index.resolve_field(owner, name, descriptor)
            symbol = "{} {}.{}:{}".format(opcode, owner, name, descriptor)
            if not found:
                results.append((addon_id, statuses[addon_id], "MISSING_FIELD", symbol))
                continue
            _, access = found
            expected_static = opcode in ("GETSTATIC", "PUTSTATIC", "HANDLE2", "HANDLE4")
            if bool(access & ACC_STATIC) != expected_static:
                results.append((addon_id, statuses[addon_id], "FIELD_STATIC_MISMATCH", symbol))
            elif not access & ACC_PUBLIC:
                results.append((addon_id, statuses[addon_id], "FIELD_ACCESS", symbol))
        for opcode, owner, name, descriptor, tag in sorted(demands["methods"]):
            found = index.resolve_method(owner, name, descriptor)
            symbol = "{} {}.{}{}".format(opcode, owner, name, descriptor)
            if not found:
                results.append((addon_id, statuses[addon_id], "MISSING_METHOD", symbol))
                continue
            _, access = found
            expected_static = opcode == "INVOKESTATIC" or opcode.startswith("HANDLE") and tag in (6,)
            if bool(access & ACC_STATIC) != expected_static:
                results.append((addon_id, statuses[addon_id], "METHOD_STATIC_MISMATCH", symbol))
            elif not access & ACC_PUBLIC:
                results.append((addon_id, statuses[addon_id], "METHOD_ACCESS", symbol))
            owner_class = index.classes.get(owner)
            if owner_class:
                owner_is_interface = bool(owner_class.access & ACC_INTERFACE)
                if tag == 11 and not owner_is_interface:
                    results.append((addon_id, statuses[addon_id], "OWNER_NOT_INTERFACE", symbol))
                elif tag == 10 and owner_is_interface:
                    results.append((addon_id, statuses[addon_id], "OWNER_IS_INTERFACE", symbol))
        for owner, name, descriptor, kind in sorted(demands["mixins"]):
            symbol = "{} {}.{}{}".format(kind, owner, name, descriptor)
            if owner not in index.classes:
                results.append((addon_id, statuses[addon_id], "MIXIN_CLASS", symbol))
            elif not index.resolve_method(owner, name, descriptor):
                results.append((addon_id, statuses[addon_id], "MIXIN_METHOD", symbol))
    return results


def command_abi(args):
    lines = abi_lines(args.jar, args.prefix)
    if args.check:
        differences = check_snapshot(args.check, lines)
        if differences:
            print("TC6 ABI snapshot mismatch:", file=sys.stderr)
            for difference in differences:
                print("  " + difference, file=sys.stderr)
            return 1
        print("TC6 ABI snapshot PASSED: {} entries".format(len(lines) - 3))
        return 0
    write_lines(args.output, lines)
    print("TC6 ABI snapshot written: {} entries".format(len(lines) - 3))
    return 0


def command_abi_diff(args):
    lines = abi_gap_lines(args.donor, args.target, args.prefix)
    if args.check:
        differences = check_snapshot(args.check, lines)
        if differences:
            print("TC6 donor-to-target ABI gap snapshot mismatch:", file=sys.stderr)
            for difference in differences:
                print("  " + difference, file=sys.stderr)
            return 1
    else:
        write_lines(args.output, lines)
    action = "verified" if args.check else "written"
    print("TC6 donor-to-target ABI gaps {}: {} gaps".format(action, len(lines) - 2))
    return 0


def command_demand(args):
    entries, all_demands, lines = demand_lines(args.manifest, args.jar_dir)
    if args.check:
        differences = check_snapshot(args.check, lines)
        if differences:
            print("TC6 addon demand snapshot mismatch:", file=sys.stderr)
            for difference in differences:
                print("  " + difference, file=sys.stderr)
            return 1
    else:
        write_lines(args.output, lines)

    index = ClassIndex([args.target] + args.classpath, args.mappings)
    results = resolve_demands(entries, all_demands, index)
    supported_failures = [result for result in results if result[1] == "supported"]
    candidate_failures = [result for result in results if result[1] == "candidate"]
    candidate_symbols = set((result[2], result[3]) for result in candidate_failures)
    for addon_id, status, reason, symbol in results:
        print("{} {} {} {}".format(status.upper(), addon_id, reason, symbol))
    if supported_failures:
        print("TC6 addon resolution FAILED: {} supported gaps".format(len(supported_failures)), file=sys.stderr)
        return 1
    if args.fail_candidates and candidate_failures:
        print("TC6 addon resolution FAILED: {} candidate gaps".format(len(candidate_symbols)), file=sys.stderr)
        return 1
    action = "verified" if args.check else "written"
    print(
        "TC6 addon demand {}: {} addons, {} entries, {} candidate gaps".format(
            action, len(entries), len(lines) - 1, len(candidate_symbols)
        )
    )
    return 0


def command_verify_remap(args):
    failures = compare_remapped_jars(args.source, args.remapped)
    demands = member_demands(args.remapped, "net/minecraft/")
    srg_fields, srg_methods = srg_member_names(args.mappings)
    for opcode, owner, name, descriptor, _ in sorted(demands["fields"]):
        if name in srg_fields:
            failures.append("SRG field remains: {} {}.{}:{}".format(opcode, owner, name, descriptor))
    for opcode, owner, name, descriptor, _ in sorted(demands["methods"]):
        if (name, descriptor) in srg_methods:
            failures.append("SRG method remains: {} {}.{}{}".format(opcode, owner, name, descriptor))
    if failures:
        print("Smoke remap verification FAILED for {}:".format(args.remapped), file=sys.stderr)
        for failure in failures[:20]:
            print("  " + failure, file=sys.stderr)
        return 1
    with zipfile.ZipFile(args.remapped) as remapped_jar:
        entry_count = len(remapped_jar.namelist())
    print(
        "Smoke remap verification PASSED: {} resources/classes preserved; {} Minecraft owners contain no mapped SRG refs".format(
            entry_count, len(demands["classes"])
        )
    )
    return 0


def command_target(args):
    lines, counts = semantic_target_lines(args.demand, args.policy)
    if args.check:
        differences = check_snapshot(args.check, lines)
        if differences:
            print("TC6 semantic target mismatch:", file=sys.stderr)
            for difference in differences:
                print("  " + difference, file=sys.stderr)
            return 1
    else:
        write_lines(args.output, lines)
    action = "verified" if args.check else "written"
    summary = " ".join("{}={}".format(level, counts[level]) for level in SEMANTIC_LEVELS)
    print("TC6 semantic target {}: {} symbols; {}".format(action, len(lines) - 1, summary))
    return 0


def build_parser():
    parser = argparse.ArgumentParser(description=__doc__)
    subparsers = parser.add_subparsers(dest="command")
    abi = subparsers.add_parser("abi", help="extract or verify a public/protected ABI snapshot")
    abi.add_argument("--jar", required=True)
    abi.add_argument("--prefix", default="thaumcraft/api/")
    output = abi.add_mutually_exclusive_group(required=True)
    output.add_argument("--output")
    output.add_argument("--check")
    abi.set_defaults(handler=command_abi)
    abi_diff = subparsers.add_parser("abi-diff", help="extract or verify donor-to-target public ABI gaps")
    abi_diff.add_argument("--donor", required=True)
    abi_diff.add_argument("--target", required=True)
    abi_diff.add_argument("--prefix", default="thaumcraft/api/")
    abi_diff_output = abi_diff.add_mutually_exclusive_group(required=True)
    abi_diff_output.add_argument("--output")
    abi_diff_output.add_argument("--check")
    abi_diff.set_defaults(handler=command_abi_diff)
    demand = subparsers.add_parser("demand", help="extract and resolve pinned addon Thaumcraft references")
    demand.add_argument("--manifest", required=True)
    demand.add_argument("--jar-dir", required=True)
    demand.add_argument("--target", required=True)
    demand.add_argument("--classpath", action="append", default=[])
    demand.add_argument("--mappings")
    demand.add_argument("--fail-candidates", action="store_true")
    demand_output = demand.add_mutually_exclusive_group(required=True)
    demand_output.add_argument("--output")
    demand_output.add_argument("--check")
    demand.set_defaults(handler=command_demand)
    remap = subparsers.add_parser("verify-remap", help="verify a dev-remapped addon jar")
    remap.add_argument("--source", required=True)
    remap.add_argument("--remapped", required=True)
    remap.add_argument("--mappings", required=True)
    remap.set_defaults(handler=command_verify_remap)
    target = subparsers.add_parser("target", help="classify every pinned addon symbol by semantic support level")
    target.add_argument("--demand", required=True)
    target.add_argument("--policy", required=True)
    target_output = target.add_mutually_exclusive_group(required=True)
    target_output.add_argument("--output")
    target_output.add_argument("--check")
    target.set_defaults(handler=command_target)
    return parser


def main(argv=None):
    parser = build_parser()
    args = parser.parse_args(argv)
    if not getattr(args, "handler", None):
        parser.print_help()
        return 2
    try:
        return args.handler(args)
    except (ClassFormatError, IOError, OSError, ValueError, zipfile.BadZipFile) as error:
        print("tc6-compat: {}".format(error), file=sys.stderr)
        return 1


if __name__ == "__main__":
    sys.exit(main())
