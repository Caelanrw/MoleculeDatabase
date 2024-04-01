from pysmiles import read_smiles
import networkx as nx
import random

# returns the order number of the edge that appears using nx.edge_list
def getOrder(line):
     return int(line.split("{'order': ")[1].rstrip('}\n'))

# multiplies the edge pair by the order number
def processContent(content):
    new_content = ""
    for line in content:
        # Check if the line contains bond order information
        if "{'order':" in line:
            bond_order = getOrder(line)
            vertex_pair = ' '.join(line.split()[:2])
            for i in range(bond_order):
             new_content += vertex_pair + '\n'
        else:
            new_content += line
    return new_content


def scrambleLabels(graph):
    # create a mapping from old labels to new labels
    nodes = list(graph.nodes())
    shuffled_nodes = nodes.copy()
    random.shuffle(shuffled_nodes)
    new_map = dict(zip(nodes, shuffled_nodes))
    
    # Use the mapping to generate a new graph with shuffled labels
    scrambled_graph = nx.relabel_nodes(graph, new_map)
    return scrambled_graph

def writeMolecule(mol_name, smiles):
    mol_with_H = read_smiles(smiles, explicit_hydrogen=True)
    file_path_mol = "./molecules/" + mol_name + ".txt"
    
    nx.write_edgelist(mol_with_H, file_path_mol)
    
    # write metadata for mol
    nodes = mol_with_H.nodes(data='element')
    mol_data = mol_name + '\n' + str(len(nodes)) + '\n'
    for node in nodes:
      mol_data += node[1] + '\n'
      
      
    # Read the existing contents of the file
    with open(file_path_mol, 'r') as file:
        edgelist = file.read()
        
    modified_edgelist = processContent(edgelist.split('\n'))
    # Write the new content at the beginning and the original content
    with open(file_path_mol, 'w') as file:
        file.write(mol_data + modified_edgelist)

def writeIsomorphic(mol_name, smiles):
    iso_graph = scrambleLabels(read_smiles(smiles, explicit_hydrogen=True))
    file_path_iso = "./isomorphic_test/" + mol_name + "_iso.txt"
    
    nx.write_edgelist(iso_graph, file_path_iso)
    
    # write metadata for isomorphic mol
    nodes = sorted(iso_graph.nodes(data="element"))
    iso_data = mol_name + '\n' + str(len(nodes)) + '\n'
    for node in nodes:
        iso_data += node[1] + '\n'
        
    with open(file_path_iso, 'r') as file:
        isolist = file.read()
        
    modified_isolist = processContent(isolist.split('\n'))
    with open(file_path_iso, 'w') as file:
        file.write(iso_data + modified_isolist)

if __name__ == "__main__":
    # replace with name of molecule
    mol_name = "adenine"
    
    # replace with smiles string of molecule
    smiles = 'c1[nH]c(c-2ncnc2n1)N'
    
    writeMolecule(mol_name, smiles)
    writeIsomorphic(mol_name, smiles)
    