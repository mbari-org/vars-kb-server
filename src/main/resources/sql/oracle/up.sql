WITH org_name (parent_id, parent_name, parent_rank, child_id, child_name, child_rank) AS
    (
        SELECT DISTINCT
            parent.id AS parent_id,
            parentname.ConceptName as parent_name,
            coalesce(parent.RankLevel, '') || coalesce(parent.RankName, '') AS parent_rank,
            child.id AS child_id,
            childname.ConceptName as child_name,
            coalesce(child.RankLevel, '') || coalesce(child.RankName, '') AS child_rank
        FROM
            Concept parent RIGHT OUTER JOIN
            Concept child ON child.ParentConceptID_FK = parent.id LEFT OUTER JOIN
            ConceptName childname ON childname.ConceptID_FK = child.id LEFT OUTER JOIN
            ConceptName parentname ON parentname.ConceptID_FK = parent.id
        WHERE
            LOWER(childname.NameType) LIKE LOWER('primary') AND
            LOWER(parentname.NameType) LIKE LOWER('primary')
    ),
    jn (parent_id, parent_name, parent_rank, child_id, child_name, child_rank) AS
    (
        SELECT
            parent_id,
            parent_name,
            parent_rank,
            child_id,
            child_name,
            child_rank
        FROM
            org_name
        WHERE
            parent_name = ?
        UNION ALL
            SELECT
                org_name.parent_id,
                org_name.parent_name,
                org_name.parent_rank,
                org_name.child_id,
                org_name.child_name,
                org_name.child_rank
            FROM
                jn JOIN
                org_name ON  org_name.child_id = jn.parent_id
    )
SELECT DISTINCT
    jn.parent_id,
    jn.parent_name,
    jn.parent_rank,
    jn.child_id,
    jn.child_name,
    jn.child_rank
FROM
    jn
ORDER BY
    1