# Group4

# Commands


- ./md --addMolecule [FILEPATH]: Add a new molecule to the database. This command does not return anything.
- ./md --findMolecule [FILEPATH]: Searches for a molecule in the database. It prints "FOUND" if the molecule exists (in verbose mode) or "NOT FOUND" if it does not.
- --quit: Quit the program.
- --printDb: Prints the current state of the molecule database to the terminal.
- --verbose: Toggles the verbose mode of the program. If verbose mode is enabled, the program prints additional information to the terminal during its operation. If the molecule object being added is null, it prints: "molecule == null". When searching for a molecule, if there is no list of molecules with the same number of atoms as the molecule being searched for, it prints: "no ArrayList with correct # of atoms". For each molecule it compares with the target molecule in the database, it prints: "<dbMolecule.moleculeName> vs <molecule.moleculeName>". On successful loading of the database from a file, it prints: "Database loaded successfully.". If there's an error during the loading process (e.g., file not found, deserialization error), it prints: "Error loading database: " + e.getMessage(). 
