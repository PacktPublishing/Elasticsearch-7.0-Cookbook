# importing the libraries
import numpy as np
import matplotlib.pyplot as plt
import pandas as pd

import elasticsearch
es = elasticsearch.Elasticsearch()
index_name = "iris"
result = es.search(index="iris", size=100)

x = []
for hit in result["hits"]["hits"]:
    source = hit["_source"]
    x.append(np.array([source['f1'], source['f2'], source['f3'], source['f4']]))
x = np.array(x)

# Finding the optimum number of clusters for k-means classification
from sklearn.cluster import KMeans
# Applying kmeans to the dataset / Creating the kmeans classifier
kmeans = KMeans(n_clusters=3, init='k-means++', max_iter=300, n_init=10, random_state=0)
y_kmeans = kmeans.fit_predict(x)
# Visualising the clusters
plt.scatter(x[y_kmeans == 0, 0], x[y_kmeans == 0, 1], s=100, c='red', label='Iris-setosa')
plt.scatter(x[y_kmeans == 1, 0], x[y_kmeans == 1, 1], s=100, c='blue', label='Iris-versicolour')
plt.scatter(x[y_kmeans == 2, 0], x[y_kmeans == 2, 1], s=100, c='green', label='Iris-virginica')

# Plotting the centroids of the clusters
plt.scatter(kmeans.cluster_centers_[:, 0], kmeans.cluster_centers_[:, 1], s=100, c='yellow', label='Centroids')

plt.legend()
plt.show()
