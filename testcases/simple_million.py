import molecule_input as mi
import random
import requests
import time

ASPIRIN = "C9H8O4"
DOXYCYCLINE = "C22H24N2O8"
IBUPROFEN = "C13H18O2"
LORATADINE = "C22H23ClN2O2"
PARACETAMOL = "C8H9NO2"
VANCOMYCIN = "C66H75Cl2N9O24"

FOSFOMYCIN = "C3H7O4P"
LINEZOLID = "C16H20FN3O4"

MIN_ISOMER_COUNT = 500

MAX_CID_SEGMENT_LEN = 4000  # maybe 3997
MAX_MOLECULE_COUNT = 10_000_000

C_count_max = 56
C_count_min = 16

H_C_ratio_max = 48 / 25
H_C_ratio_min = 17 / 15

Cl_C_ratio_max = 1 / 30
Cl_C_ratio_min = 0
Cl_range = [0, 1]

F_C_ratio_max = 1 / 16
F_C_ratio_min = 0
F_range = [0, 1]

N_C_ratio_max = 4 / 13
N_C_ratio_min = 0

O_C_ratio_max = 2 / 5
O_C_ratio_min = 1 / 4

P_C_ratio_max = 1 / 3
P_C_ratio_min = 0
P_range = [0, 1]

S_C_ratio_max = 1 / 15
S_C_ratio_min = 0
S_range = [0, 1, 2]


def add_atom(atom: str, C_count: int, ratio_max: float, ratio_min: float):
    ret = ""
    ratio = ratio_min + random.random() * (ratio_max - ratio_min)
    count = int(C_count * ratio)
    if count > 1:
        ret += atom + str(count)
    elif count == 1:
        ret += atom
    return ret


def add_atom2(atom: str, range: list):
    ret = ""
    count = random.choice(range)
    if count > 1:
        ret += atom + str(count)
    elif count == 0:
        ret += atom
    return ret


def wait_for_api(previous_time):
    if previous_time != None:
        while time.time() - previous_time < 0.2:
            pass
    return time.time()


if __name__ == "__main__":

    previous_time = None

    previous_formulae = [""]
    molecule_count = 0
    while molecule_count < MAX_MOLECULE_COUNT:

        chemical_formula = ""
        while chemical_formula in previous_formulae:
            chemical_formula = ""
            C_count = random.randint(C_count_min, C_count_max)
            chemical_formula += "C" + str(C_count)
            chemical_formula += add_atom("H", C_count, H_C_ratio_max, H_C_ratio_min)
            # chemical_formula += add_atom("Cl", C_count, Cl_C_ratio_max, Cl_C_ratio_min)
            # chemical_formula += add_atom("F", C_count, F_C_ratio_max, F_C_ratio_min)
            chemical_formula += add_atom("N", C_count, N_C_ratio_max, N_C_ratio_min)
            chemical_formula += add_atom("O", C_count, O_C_ratio_max, O_C_ratio_min)
            # chemical_formula += add_atom("P", C_count, P_C_ratio_max, P_C_ratio_min)
            # chemical_formula += add_atom("S", C_count, S_C_ratio_max, S_C_ratio_min)

        url = (
            "https://pubchem.ncbi.nlm.nih.gov/rest/pug/compound/fastformula/"
            + chemical_formula
            + "/cids/JSON"
        )
        previous_time = wait_for_api(previous_time)
        response = requests.get(url)
        if response.status_code != 200:
            continue

        data = response.json()
        if "Fault" in data:
            print(chemical_formula + ": " + data["Fault"]["Message"])
            previous_formulae.append(chemical_formula)
            continue

        cids = data["IdentifierList"]["CID"]
        isomer_count = len(cids)
        if isomer_count < MIN_ISOMER_COUNT:
            print(chemical_formula + ": Has only " + str(isomer_count) + " isomers")
            previous_formulae.append(chemical_formula)
            continue

        cid_segment = None
        cid_idx = 0
        while True:
            cid_segment = str(cids[cid_idx])
            cid_idx += 1
            if cid_idx == isomer_count:
                break
            cid = cids[cid_idx]
            while len(cid_segment + "," + str(cid)) < MAX_CID_SEGMENT_LEN:
                cid_segment += "," + str(cid)
                cid_idx += 1
                cid = cids[cid_idx]
            url = (
                "https://pubchem.ncbi.nlm.nih.gov/rest/pug/compound/cid/"
                + cid_segment
                + "/property/Title,CanonicalSMILES/json"
            )
            previous_time = wait_for_api(previous_time)
            response = requests.get(url)

            data = response.json()
            for molecule_struct in data["PropertyTable"]["Properties"]:
                if "CanonicalSMILES" in molecule_struct:
                    molecule_name = chemical_formula + "_" + str(molecule_struct["CID"])
                    smiles = molecule_struct["CanonicalSMILES"]
                    if mi.writeMolecule(molecule_name, smiles, "simple_molecules") == 0:
                        molecule_count += 1
                        mi.writeIsomorphic(molecule_name, smiles, "simple_isomorphic")
                        
    print("goodbye")
