from pysmiles import read_smiles
import networkx as nx
import random
import requests
import os
import time

# returns the order number of the edge that appears using nx.edge_list
def getOrder(line):
    # only allow integer bond order values
    order = line.split("{'order': ")[1].rstrip('}\n')
    return int(line.split("{'order': ")[1].rstrip('}\n')) if order.isdigit() else -1

# multiplies the edge pair by the order number
def processContent(content):
    new_content = ""
    for line in content:
        # check if the line contains bond order information
        if "{'order':" in line:
            bond_order = getOrder(line)
            # if bond order is invalid, then return error
            if bond_order == -1:
                return -1
            
            # duplicate the edge for value of bond order
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
    
    # use the mapping to generate a new graph with shuffled labels
    scrambled_graph = nx.relabel_nodes(graph, new_map)
    return scrambled_graph

def writeMolecule(mol_name, smiles, folder_name="molecules"):
    mol_with_H = read_smiles(smiles, explicit_hydrogen=True)
    file_path_mol = "./" + folder_name + "/" + mol_name + ".txt"
    
    nx.write_edgelist(mol_with_H, file_path_mol)
    
    # write metadata for mol
    nodes = mol_with_H.nodes(data='element')
    mol_data = mol_name + '\n' + str(len(nodes)) + '\n'
    for node in nodes:
      mol_data += node[1] + '\n'
      
      
    # read the generated edgelist
    with open(file_path_mol, 'r') as file:
        edgelist = file.read()
    
    # if theres an invalid edgelist, remove it from the file system
    modified_edgelist = processContent(edgelist.split('\n'))
    if modified_edgelist == -1:
        os.remove(file_path_mol)
        return -1
        
    # write metadata at the beginning and the original content
    with open(file_path_mol, 'w') as file:
        file.write(mol_data + modified_edgelist)
    
    return 0

def writeIsomorphic(mol_name, smiles, folder_name="isomorphic_test"):
    iso_graph = scrambleLabels(read_smiles(smiles, explicit_hydrogen=True))
    file_path_iso = "./" + folder_name + "/" + mol_name + "_iso.txt"
    
    nx.write_edgelist(iso_graph, file_path_iso)
    
    # write metadata for isomorphic mol
    nodes = sorted(iso_graph.nodes(data="element"))
    iso_data = mol_name + '\n' + str(len(nodes)) + '\n'
    for node in nodes:
        iso_data += node[1] + '\n'
        
    with open(file_path_iso, 'r') as file:
        isolist = file.read()
        
    modified_isolist = processContent(isolist.split('\n'))
    if modified_isolist == -1:
        os.remove(file_path_iso)
        return -1
    
    with open(file_path_iso, 'w') as file:
        file.write(iso_data + modified_isolist)
        
    return 0

if __name__ == "__main__":
    START_CIN = 40000
    STOP_CIN = 41000
    INCREMENT = 100
    # HH = 783
    # H2 = 24523

    # range(start, end, step) --> Change values for number of molecules required
    for indx in range(START_CIN,STOP_CIN,INCREMENT):
        start_time = time.time()

        numbers = [str(i) for i in range(indx, indx + INCREMENT)]
        # if HH in numbers:
        #     numbers.remove(HH)
        indexes = ",".join(numbers)
        
        # query from pubchem URL
        url = 'https://pubchem.ncbi.nlm.nih.gov/rest/pug/compound/cid/' + indexes + '/property/Title,CanonicalSMILES/json'
        response = requests.get(url)

        # Check if the request was successful
        if response.status_code == 200:
            # Extract the json from the response
            page_text = response.json()
            
            for chemical in page_text['PropertyTable']['Properties']:
                # check if desired keys are in the json
                if 'CanonicalSMILES' in chemical: # and 'Title' in chemical:
                    # mol_name = chemical['Title']
                    mol_name = "molecule" + str(chemical['CID'])
                    smiles = chemical['CanonicalSMILES']
                    if smiles == "[HH]":
                        continue
                    
                    print("molecule "+ str(chemical['CID']) + ": " +  mol_name + "\t" + "smiles: " + smiles)
                
                    if writeMolecule(mol_name, smiles) == 0:
                        writeIsomorphic(mol_name, smiles)
        else:
            print("Failed to retrieve the page. Status code:", response.status_code)

        while (time.time() - start_time < 0.3):
            pass
