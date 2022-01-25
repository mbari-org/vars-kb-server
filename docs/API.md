# API

MBARI Media Management (M3) uses a collection of services to support various video applications. One service is the vars-kb-server which supplies information about what concepts are used for annotating video, as well as phylogenetic structure that can be used to assist with various types of analysis. This service is a very simple RESTful service. All requests are made using HTTP GET requests.

## tl;dr

All requests return JSON objects.

`GET /v1/concept` - Return all concept names. e.g. <http://dsg.mbari.org/kb/v1/concept>
`GET /v1/concept/{name}` - Return the details (alternate names, media, descriptors) for the given concept {name}. e.g. <http://dsg.mbari.org/kb/v1/concept/Pandalus%20platyceros>
`GET /v1/concept/children/{name}` - Return the children of the given concept `{name}`. <http://dsg.mbari.org/kb/v1/concept/squid>
`GET /v1/concept/find/{glob}` - Find concepts whose name or alternative names contains the provided characters. (e.g. red). <http://dsg.mbari.org/kb/v1/concept/find/red>
`GET /v1/concept/parent/{name}` - Return the parent of the given concept `{name}`. e.g. <http://dsg.mbari.org/kb/v1/concept/parent/crab>
`GET /v1/concept/root` - Return the details for the root concept of the knowledgebase. <http://dsg.mbari.org/kb/v1/concept/root>
`GET /v1/dsg/images/representative/{name}` - Returns a random sample of representative images for this node from its child concepts. <http://dsg.mbari.org/kb/v1/dsg/images/representative/squid>
`GET /v1/health` - Returns application info. Also useful for checking that the service is up.
`GET /v1/history/approved`- Returns all approved changes to the knowledgebase
`GET /v1/history/pending` - Returns all changes to the knowledebase that are pending approval by an administrator.
`GET /v1/links/{name}/using/{linkname}` - Returns all link templates for the concept {name} that have the given {linkname}. e.g <http://dsg.mbari.org/kb/v1/links/Nanomia/using/surface-color>
`GET /v1/links/{name}` - Return all link templates that can be applied to the given concept.
`GET /v1/links/query/linkrealizations/{linkname}` - Link realizations are descriptive info about a concept useful for descriptive guides. <http://dsg.mbari.org/kb/v1/links/query/linkrealizations/dsg-range>
`GET /v1/phylogeny/basic/{name}` - Get a flattened taxa branch from the root of the knowledgebase down to the named concept {name}. They are ordered; i.e. the top node is the kb root, the bottom node is the named concept  
`GET /v1/phylogeny/down/{name}` - Return the descendents of 'name' (includes 'name')
`GET /v1/phylogeny/siblings/{name}` - Return the siblings of 'name' (includes 'name'). Siblings share the same parent concept. e.g. <http://dsg.mbari.org/kb/v1/phylogeny/siblings/Nanomia>  
`GET /v1/phylogeny/taxa/{name}` - Get a list of all concepts that includes the named concept and all of it's descendants. They are alphabetically ordered by concept name  
`GET /v1/phylogeny/up/{name}` - Return the ancestors of 'name' (includes 'name')  

## Detailed API

### List all Concepts

<http://dsg.mbari.org/kb/v1/concept>

This call lists all concepts, including synonyms and common names. The return is a JSON array of Strings:

```json
[
  "1-gallon paint bucket",
  "2G Robotics structured light laser",
  "55-gallon drum",
  "a\u0027a",
  "abandoned research equipment",
  "Abraliopsis",
  "Abraliopsis felis",
  "abyssal",
  ...
]
```

### List Details about a Concept

`http://dsg.mbari.org/kb/v1/concept/{name}`

This will return a JSON structure that includes the main name for the concept, alternate names (synonyms, former names, common names), the rank (such a genus, species, etc), links to images of the concept, and descriptive details.

Example:

<http://dsg.mbari.org/kb/v1/concept/Pandalus%20platyceros>

```json
{
  "name": "Pandalus platyceros",
  "alternateNames": [
    "spot prawn",
    "spot shrimp"
  ],
  "rank": "species",
  "media": [
    {
      "url": "http://dsg.mbari.org/images/dsg/external/Arthropoda/Pandalus_platyceros_02.jpg",
      "caption": "",
      "credit": "MBARI 2001: V2013-03",
      "mimeType": "image/jpg",
      "isPrimary": false
    },
    {
      "url": "http://dsg.mbari.org/images/dsg/external/Arthropoda/Pandalus_platyceros_01.jpg",
      "caption": "",
      "credit": "MBARI 2001: V2013-03",
      "mimeType": "image/jpg",
      "isPrimary": true
    }
  ],
  "descriptors": [
    {
      "linkName": "dsg-depth-distribution",
      "toConcept": "self",
      "linkValue": "4 - 487 m"
    },
    ...
    {
      "linkName": "dsg-reference",
      "toConcept": "self",
      "linkValue": "Schlining, K.L. (1999). The spot prawn (Pandalus platyceros Brandt 1851) resource in Carmel Submarine Canyon, California: Aspects of fisheries and habitat associations. M.S. Thesis, California State University, Stanislaus."
    }

  ]
}
```

### List Phylogenetic Ancestors of a Concept

`http://dsg.mbari.org/kb/v1/phylogeny/up/{name}`

This call will return a nested tree representing the phylogenetic hierarchy to the concept. It includes primary names (no synonyms) and their rank

Example:

<http://dsg.mbari.org/kb/v1/phylogeny/up/Cnidaria>

```json
{
  "name": "object",
  "rank": "",
  "children": [
    {
      "name": "physical object",
      "rank": "",
      "children": [
        {
          "name": "marine organism",
          "rank": "",
          "children": [
            {
              "name": "Eukaryota",
              "rank": "superkingdom",
              "children": [
                {
                  "name": "Animalia",
                  "rank": "kingdom",
                  "children": [
                    {
                      "name": "Cnidaria",
                      "rank": "phylum",
                      "children": []
                    }
                  ]
                }
              ]
            }
          ]
        }
      ]
    }
  ]
}
```

### List Phylogenetic Descendants of a Concept

`http://dsg.mbari.org/kb/v1/phylogeny/down/{name}`

Returns a nested list representing the phylogenetic tree below a concept (i.e. The concepts descendants). The data structure is exactly the same as lising the ancestors of a concept.

Example:

<http://dsg.mbari.org/kb/v1/phylogeny/down/Apolemiidae>

```json
{
  "name": "Apolemiidae",
  "rank": "family",
  "children": [
    {
      "name": "Apolemia",
      "rank": "genus",
      "children": [
        {
          "name": "Apolemia lanosa",
          "rank": "species",
          "children": []
        },
        {
          "name": "Apolemia uvaria",
          "rank": "species",
          "children": []
        },
        {
          "name": "Apolemia rubriversa",
          "rank": "species",
          "children": []
        }
      ]
    }
  ]
}
```
