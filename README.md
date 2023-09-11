# IR from Greek Documents

Author: Varsamis Haralampos  
Subject: Information Retrieval from Greek Documents: Evaluations and Improvements  

## Table Of Contents

- [IR from Greek Documents](#ir-from-greek-documents)
  - [Table Of Contents](#table-of-contents)
  - [Introduction](#introduction)
  - [Dataset](#dataset)
    - [Document Format](#document-format)
    - [Query Format](#query-format)
  - [Search Engines Evaluations](#search-engines-evaluations)
    - [Creating the index in Elastic Search](#creating-the-index-in-elastic-search)
      - [Settings Example](#settings-example)
      - [Mappings Example](#mappings-example)
    - [Indexing in Elastic Search](#indexing-in-elastic-search)
      - [ES - Required Format - Bulk Indexing](#es---required-format---bulk-indexing)
      - [ES - Indexing example](#es---indexing-example)
    - [Querying in Elastic Search](#querying-in-elastic-search)
      - [ES - Querying Required Format](#es---querying-required-format)
    - [Indexing in Solr](#indexing-in-solr)
    - [Indexing in Algolia](#indexing-in-algolia)
  - [Improvements on misspelled Queries](#improvements-on-misspelled-queries)

## Introduction

There are three scales in the bachelor thesis. The dataset's development, search engines evaluations on the dataset, and changes that may be applied to a misspelled query to repair the query and improve the documents retrieved. The methods used to collect and evaluate data, the metrics used to evaluate search engine querying results, and the various approaches to improving misspelled queries will be described in detail in the sections that follow.

## Dataset

The dataset was not found online and had to be created by hand (could not find online datasets with queries,documents and relevance factors).

The queries were chosen from the [TREC Fair Ranking 2020](https://drive.google.com/drive/folders/1JDQ35ECAOup1BuJ9DLehRHHlsozkwIaz), which were translated to Greek. The documents were selected by searching data on each query and keeping 1-3 documents that appeared to be the most relevant(judged as relevant by human).

### Document Format

| ID                                    | Title                     | Body                              |
| :------------------------------------ | :------------------------ | :-------------------------------- |
| The unique identifier of the Document | The title of the document | the body/abstract of the document |

### Query Format

| ID                                  | Query                                                      | RelevantDocs                                                                                                                                   |
| :---------------------------------- | :--------------------------------------------------------- | :--------------------------------------------------------------------------------------------------------------------------------------------- |
| The uniquer identifier of the Query | The query to be searched in the documents(one or more words) | A List of Document IDs and their relevance Rank(each documents ranks at a position, 0 means most relevant,1 means second most relevant,etc...) |

## Search Engines Evaluations

We used 3 search engines to be evaluated:

- Elasticsearch
- Solr
- Algolia

### Creating the index in Elastic Search

The index is mostly made up of mappings and settings. Mappings are index properties (ID,Title,Body), and settings are the rules that are applied during the indexing and querying process. It is worth noting that an analyzer can be specified within the settings, which can be responsible for applying stemming, removing stopwords, converting all words to lowercase, or anything else that the search engine may support.

#### Settings Example

```json
"settings": {
    "analysis": {
      "filter": {
        "greek_stop": {
          "type":       "stop",
          "stopwords":  "_greek_" 
        },
        "greek_lowercase": {
          "type":       "lowercase",
          "language":   "greek"
        },
        "greek_stemmer": {
          "type":       "stemmer",
          "language":   "greek"
        }
      },
      "analyzer": {
        "my_greek_analyzer": {
          "tokenizer":  "standard",
          "filter": [
            "greek_lowercase",
            "greek_stop",
            "greek_stemmer"
          ]
        }
      }
    }
  }
```

In the example above 3 filters are created:

- greek_stop: a stop filter for the Greek language that removes stopwords.
- greek_lowercase: a lowercase filter for the Greek language that converts all words to lowercase
- greek_stemmer: Greek language stemmer filter.
  
These 3 filters are then used to create an analyzer

- my_greek_analyzer: The name of the analyzer that can be used to transform input data
- standard tokenizer: Removes most punctuation symbols.

You may search for other [Analyzers](https://www.elastic.co/guide/en/elasticsearch/reference/current/analysis-analyzers.html) and [Tokenizers](https://www.elastic.co/guide/en/elasticsearch/reference/current/analysis-tokenizers.html#analysis-tokenizers)

#### Mappings Example

```json
"mappings": {
      "properties": {
          "ID": {"type":"integer"},
          "Title" :{"type":"text","analyzer": "my_greek_analyzer"},
          "Body": {"type":"text","analyzer": "my_greek_analyzer"}
    }
  }
```

In the example above,an index is defined with properties

- ID
- Title
- Body

And the ones that have "type:text" are parsed with the rules set to the "analyzer:my_greek_analyzer"

### Indexing in Elastic Search

#### ES - Required Format - Bulk Indexing

When bulk indexing the required format is:

```json
{ "index" : { "_index" : "...", "_id" : "0" } }
{"ID":0,"Title":"...","Body":"..."}
{ "index" : { "_index" : "...", "_id" : "1" } }
{"ID":1,"Title":"...","Body":"..."}
...
```

Where the first line defines the index for each row that follows.

- "_index","..." : The name of the index
- "_id" : The identifier of the record(optional)

The second line holds the data entry (each field is listed in [Document Format](#document-format)).

This process is repeated for each document to be indexed.

#### ES - Indexing example

Creating a json file with the required format:

```json
{ "index" : { "_index" : "dataset", "_id" : "0" } }
{"ID":0,"Title":"Αλγόριθμοι","Body":"..."}
{ "index" : { "_index" : "dataset", "_id" : "1" } }
{"ID":1,"Title":"Φάρμακα","Body":"..."}
```

and then executing a powershell or curl command to bulk index:

```powershell
Invoke-RestMethod "https://localhost:9200/dataset/_bulk?pretty" -Method Post -ContentType 'application/x-ndjson' -InFile ".\dataset.json" -Credential $credential
```

Note : Credentials are needed when the communication uses https. In case of http, credentials are not used.

### Querying in Elastic Search

Querying is possible via the Kibana developer tools console and REST requests. A script was mainly used due to the large number of queries (instead of the console)

## Improvements on misspelled Queries

See the final documented file in the Docu folder.

## SoundexGR algorithm

See https://github.com/YannisTzitzikas/SoundexGR
