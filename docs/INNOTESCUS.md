# [Innotescus](https://innotescus.io/)

This is documentation on MBARI's knowldegebase REST APIs to better inform the innotesus developers on how we work with naming and tagging of annotations.

## List all Concepts that can be used for Annotation

<http://dsg.mbari.org/kb/v1/concept>


This call lists all concepts, including synonyms and common names. The return is a JSON array of Strings:

```json
[
  "1-gallon paint bucket",
  "2G Robotics structured light laser",
  "55-gallon drum",
  "abandoned research equipment",
  "Abraliopsis",
  "Abraliopsis felis",
  "abyssal",
  ...
]
```

## List all Associations that can be used with an Annotation

```
http://dsg.mbari.org/kb/v1/links/{name}
```

An `Association` is a from of descriptive tag that can be attached to an annotation. This allows annotators to add additional information such as color, behavior, shape, the substrate, etc. Our knowledgebase provides restrictions on what associations should be applied to a term. For example, a `rock` can't be eating something. To accomodate this in applications, we provide a REST endpoint that lists the possbile associations that can be applied to a given term.

The structure of an association is:

```json
{
  linkName: "what the tag is about: e.g. eating, surface-color, upon",
  toConcept: "Target for the association. self | another concept in our knowledgebase",
  linkValue: "additional information to complete the tag. 'nil' is an empty value" 
}
```

___Examples:__

```
// For an animal, indicating that it's eating a crab
{
    "linkName": "eating",
    "toConcept": "crab",
    "linkValue": "nil"
}

// Something is red
{
    "linkName": "surface-color",
    "toConcept": "self",
    "linkValue": "red"
}

// Free form comment
{
    "linkName": "comment",
    "toConcept": "self",
    "linkValue": "Ben ... coffee is life!!"
}

```

<http://dsg.mbari.org/kb/v1/links/rock>

<http://dsg.mbari.org/kb/v1/links/Apolemiidae>

<http://dsg.mbari.org/kb/v1/links/Detritus%20Sampler>


## Examining the taxanomic hierarchy

### Find Taxonomic Ancestors of a Term

#### A _flat_ listing of taxonomic ancestors

```
http://dsg.mbari.org/kb/v1/phylogeny/basic/{name}
```

__Example:__

<http://dsg.mbari.org/kb/v1/phylogeny/basic/Cnidaria>

This returns a JSON array of terms. The first term in the array is the top of the hierarchy. Each subsequent term moves down the taxanomic heirarcy until reaching the provided name:

```json
[
  {
    "name": "object"
  },
  {
    "name": "physical object"
  },
  {
    "name": "marine organism"
  },
  {
    "name": "Eukaryota",
    "rank": "superkingdom"
  },
  {
    "name": "Animalia",
    "rank": "kingdom"
  },
  {
    "name": "Cnidaria",
    "rank": "phylum"
  }
]
```

#### A _structured_ listing of taxonomic ancestors

```
http://dsg.mbari.org/kb/v1/phylogeny/up/{name}
```

This call will return a nested tree representing the phylogenetic hierarchy to the concept. It includes primary names, as well as alternative names, and their rank.

__Example:__

<http://dsg.mbari.org/kb/v1/phylogeny/up/Cnidaria>

```json
{
  "name": "object",
  "alternativeNames": [
    "root"
  ],
  "children": [
    {
      "name": "physical object",
      "alternativeNames": [
        "physical-object"
      ],
      "children": [
        {
          "name": "marine organism",
          "alternativeNames": [
            "marine-organism"
          ],
          "children": [
            {
              "name": "Eukaryota",
              "rank": "superkingdom",
              "children": [
                {
                  "name": "Animalia",
                  "alternativeNames": [
                    "metazoa"
                  ],
                  "rank": "kingdom",
                  "children": [
                    {
                      "name": "Cnidaria",
                      "alternativeNames": [
                        "cnidarian"
                      ],
                      "rank": "phylum"
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

### Find Taxanomic Descendants of a Term

#### A _flat_ listing of taxonomic descendants

```
http://dsg.mbari.org/kb/v1/phylogeny/taxa/{name}
```

Returns the name and rank of all descendants, including the name you provided. The array is ordered alphabetically by name.

__Example:__

<http://dsg.mbari.org/kb/v1/phylogeny/taxa/Apolemia>

```json
[
  {
    "name": "Apolemia",
    "rank": "genus"
  },
  {
    "name": "Apolemia lanosa",
    "rank": "species"
  },
  {
    "name": "Apolemia rubriversa",
    "rank": "species"
  },
  {
    "name": "Apolemia uvaria",
    "rank": "species"
  },
  {
    "name": "Apolemiidae",
    "rank": "family"
  }
]
```

#### A _structured_ listing of taxonomic descendants

```
http://dsg.mbari.org/kb/v1/phylogeny/down/{name}
```

Returns a nested list representing the phylogenetic tree below a concept (i.e. The concepts descendants). The data structure is exactly the same as lising the ancestors of a concept. It can include `alternativeNames` for each taxanomic node in the tree.

__Example:__

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
          "name": "Apolemia uvaria",
          "rank": "species"
        },
        {
          "name": "Apolemia lanosa",
          "rank": "species"
        },
        {
          "name": "Apolemia rubriversa",
          "rank": "species"
        }
      ]
    }
  ]
}
```