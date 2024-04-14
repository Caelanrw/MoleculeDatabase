import molecule_input as mi
import random

ASPIRIN = "C9H8O4"
DOXYCYCLINE = "C22H24N2O8"
IBUPROFEN = "C13H18O2"
LORATADINE = "C22H23ClN2O2"
PARACETAMOL = "C8H9NO2"
VANCOMYCIN = "C66H75Cl2N9O24"

FOSFOMYCIN = "C3H7O4P"

C_count_max = 56
C_count_min = 3

H_C_ratio_max = 7 / 3
H_C_ratio_min = 17 / 15

Cl_range = [0, 1]
F_range = [0, 1]

N_C_ratio_max = 4 / 13
N_C_ratio_min = 0

O_C_ratio_max = 4 / 3
O_C_ratio_min = 1 / 4

P_range = [0, 1]
S_range = [0, 1, 2]


def add_H_N_O(atom: str, C_count: int, ratio_max: float, ratio_min: float):
    ret = ""
    ratio = ratio_min + random.random() * (ratio_max - ratio_min)
    count = int(C_count * ratio)
    if count > 1:
        ret += atom + str(count)
    elif count == 1:
        ret += atom
    return ret


def add_other(atom: str, range: list):
    ret = ""
    count = random.choice(range)
    if count > 1:
        ret += atom + str(count)
    elif count == 0:
        ret += atom
    return ret


if __name__ == "__main__":
    molecule_count = 0
    while molecule_count < 10_000_000:
        molecule_formula = ""

        C_count = random.randint(C_count_min, C_count_max)
        molecule_formula += "C" + str(C_count)

        molecule_formula += add_H_N_O("H", C_count, H_C_ratio_max, H_C_ratio_min)
        molecule_formula += add_other("Cl", Cl_range)
        molecule_formula += add_other("F", F_range)
        molecule_formula += add_H_N_O("N", C_count, N_C_ratio_max, N_C_ratio_min)
        molecule_formula += add_H_N_O("O", C_count, O_C_ratio_max, O_C_ratio_min)
        molecule_formula += add_other("P", P_range)
        molecule_formula += add_other("S", S_range)

        print(molecule_formula)
        break
