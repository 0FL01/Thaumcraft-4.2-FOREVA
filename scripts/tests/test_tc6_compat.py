import importlib.util
import io
import os
import tempfile
import types
import unittest


ROOT = os.path.abspath(os.path.join(os.path.dirname(__file__), "..", ".."))
SCRIPT = os.path.join(ROOT, "scripts", "tc6-compat.py")
DONOR = os.path.join(ROOT, "Thaumcraft-1.12.2-6.1.BETA26.jar")

SPEC = importlib.util.spec_from_file_location("tc6_compat", SCRIPT)
TC6 = importlib.util.module_from_spec(SPEC)
SPEC.loader.exec_module(TC6)


class Tc6CompatAbiTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.lines = TC6.abi_lines(DONOR)

    def test_donor_identity_and_java_version_are_recorded(self):
        self.assertEqual(
            "SHA256 9425f8643581b27ff8845b087c8bc6fc10425a32942f1a3f0e265ce6b38f7b5f",
            self.lines[1],
        )
        self.assertEqual("CLASSFILE_MAJOR 52", self.lines[2])

    def test_common_internals_scan_entities_descriptor_is_exact(self):
        self.assertIn(
            "F thaumcraft/api/internal/CommonInternals scanEntities Ljava/util/ArrayList; public,static",
            self.lines,
        )

    def test_non_public_inner_classes_remain_visible_in_full_shape(self):
        self.assertIn(
            "C thaumcraft/api/casters/CasterTriggerRegistry$Trigger package java/lang/Object -",
            self.lines,
        )

    def test_method_return_descriptor_is_not_erased(self):
        self.assertIn(
            "M thaumcraft/api/ThaumcraftApi getCraftingRecipes ()Ljava/util/HashMap; public,static",
            self.lines,
        )

    def test_snapshot_is_deterministic(self):
        self.assertEqual(self.lines, TC6.abi_lines(DONOR))


class Tc6CompatResolverTest(unittest.TestCase):
    @staticmethod
    def fixture_class(name, super_name="-", interfaces=None, access=TC6.ACC_PUBLIC, fields=None, methods=None):
        return types.SimpleNamespace(
            name=name,
            super_name=super_name,
            interfaces=interfaces or [],
            access=access,
            fields=fields or [],
            methods=methods or [],
        )

    def fixture_index(self):
        public_static = TC6.ACC_PUBLIC | TC6.ACC_STATIC
        base = self.fixture_class(
            "fixture/Base",
            fields=[(public_static, "VALUE", "I", [])],
            methods=[(TC6.ACC_PUBLIC, "run", "()V", [])],
        )
        child = self.fixture_class("fixture/Child", super_name="fixture/Base")
        interface = self.fixture_class(
            "fixture/Contract",
            access=TC6.ACC_PUBLIC | TC6.ACC_INTERFACE,
            methods=[(TC6.ACC_PUBLIC, "call", "()V", [])],
        )
        index = TC6.ClassIndex.__new__(TC6.ClassIndex)
        index.classes = dict((value.name, value) for value in (base, child, interface))
        index.field_aliases = {}
        index.method_aliases = {}
        return index

    def test_instruction_scanner_tracks_member_constant_pool_operands(self):
        code = bytes([178, 0, 7, 182, 0, 9, 177])
        self.assertEqual([(0, 178, 7), (3, 182, 9)], list(TC6.instruction_cp_references(code)))

    def test_jvm_resolution_walks_superclasses_and_interfaces(self):
        index = self.fixture_index()
        self.assertEqual(("fixture/Base", TC6.ACC_PUBLIC | TC6.ACC_STATIC),
                         (index.resolve_field("fixture/Child", "VALUE", "I")[0].name,
                          index.resolve_field("fixture/Child", "VALUE", "I")[1]))
        self.assertEqual("fixture/Base", index.resolve_method("fixture/Child", "run", "()V")[0].name)
        self.assertEqual("fixture/Contract", index.resolve_method("fixture/Contract", "call", "()V")[0].name)

    def test_srg_alias_resolves_when_bytecode_uses_inherited_subclass_owner(self):
        index = self.fixture_index()
        base = index.classes["fixture/Base"]
        index.method_aliases[("fixture/Base", "func_000001_a", "()V")] = (base, TC6.ACC_PUBLIC)
        resolved = index.resolve_method("fixture/Child", "func_000001_a", "()V")
        self.assertEqual("fixture/Base", resolved[0].name)

    def test_resolver_rejects_static_and_owner_kind_mismatches(self):
        index = self.fixture_index()
        entries = [{"id": "fixture", "status": "supported"}]
        demands = {
            "fixture": {
                "classes": {"fixture/Child", "fixture/Contract"},
                "fields": {("GETFIELD", "fixture/Child", "VALUE", "I", 9)},
                "methods": {("INVOKEINTERFACE", "fixture/Child", "run", "()V", 11)},
                "mixins": set(),
                "reflective": set(),
            }
        }
        reasons = [result[2] for result in TC6.resolve_demands(entries, demands, index)]
        self.assertEqual(["FIELD_STATIC_MISMATCH", "OWNER_NOT_INTERFACE"], reasons)


class Tc6SemanticTargetTest(unittest.TestCase):
    def write_fixture(self, directory, name, content):
        path = os.path.join(directory, name)
        with io.open(path, "w", encoding="utf-8") as handle:
            handle.write(content)
        return path

    def test_every_demand_is_classified_with_addon_evidence(self):
        with tempfile.TemporaryDirectory() as directory:
            demand = self.write_fixture(
                directory,
                "demand.txt",
                "FORMAT tc6-addon-demand-v1\n"
                "A addon supported server addon.jar abc\n"
                "C addon thaumcraft/api/Fixture\n"
                "M addon INVOKESTATIC thaumcraft/api/Fixture run ()V\n",
            )
            policy = self.write_fixture(
                directory,
                "policy.txt",
                "EXACT | C thaumcraft/api/* | structural\n"
                "PROJECTED | M * thaumcraft/api/* | canonical adapter\n",
            )

            lines, counts = TC6.semantic_target_lines(demand, policy)

            self.assertEqual(2, counts["EXACT"] + counts["PROJECTED"])
            self.assertIn("K EXACT C thaumcraft/api/Fixture | addon | structural", lines)
            self.assertIn(
                "K PROJECTED M INVOKESTATIC thaumcraft/api/Fixture run ()V | addon | canonical adapter",
                lines,
            )

    def test_unclassified_demand_fails_closed(self):
        with tempfile.TemporaryDirectory() as directory:
            demand = self.write_fixture(
                directory,
                "demand.txt",
                "FORMAT tc6-addon-demand-v1\nC addon thaumcraft/api/Missing\n",
            )
            policy = self.write_fixture(directory, "policy.txt", "EXACT | C other/* | structural\n")

            with self.assertRaisesRegex(ValueError, "unclassified TC6 demand"):
                TC6.semantic_target_lines(demand, policy)


if __name__ == "__main__":
    unittest.main()
