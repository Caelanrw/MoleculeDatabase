from pysmiles import read_smiles
import networkx as nx

# returns the order number of the edge that appears using nx.edge_list
def get_bond_order(line):
     return int(line.split("{'order': ")[1].rstrip('}\n'))

# multiplies the edge pair by the order number
def process_content(content):
    new_content = ""
    for line in content:
        # Check if the line contains bond order information
        if "{'order':" in line:
            bond_order = get_bond_order(line)
            vertex_pair = ' '.join(line.split()[:2])
            for i in range(bond_order):
             new_content += vertex_pair + '\n'
        else:
            new_content += line
    return new_content

if __name__ == "__main__":
    # replace with name of molecule
    mol_name = "Vitamin-C"
    
    # replace with smiles string of molecule
    smiles = 'C([C@@H]([C@@H]1C(=C(C(=O)O1)O)O)O)O'
    mol_with_H = read_smiles(smiles, explicit_hydrogen=True)
    file_path = "./molecules/" + mol_name + ".txt"

    nx.write_edgelist(mol_with_H, file_path)

    nodes = mol_with_H.nodes(data='element')
    mol_data = mol_name + '\n'
    mol_data += str(len(nodes)) + '\n'
    for a in nodes:
      mol_data += a[1] + '\n'

    # Read the existing contents of the file
    with open(file_path, 'r') as file:
        edgelist = file.read()

    modified_edgelist = process_content(edgelist.split('\n'))
    # Write the new content at the beginning and the original content
    with open(file_path, 'w') as file:
        file.write(mol_data + modified_edgelist)


