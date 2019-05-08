import elasticsearch
es = elasticsearch.Elasticsearch()
import pandas as pd

dataset = pd.read_csv('Iris.csv')
# print(dataset)
x = dataset.iloc[:, :].values
print(x)
