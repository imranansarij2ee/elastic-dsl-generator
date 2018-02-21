package com.serverless.dsl.generator.helpers;


import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public enum ElasticSearchIndices {
	NewsCMS("nbcnews", Type.NewsArticle, Type.Taxonomy, Type.Slideshow, Type.StaticPage, Type.Card, Type.Person, Type.Organization),
    TodayCMS("today", Type.NewsArticle, Type.Taxonomy, Type.Slideshow, Type.Recipe, Type.StaticPage, Type.Person, Type.Organization),
    MavisMPX("mavismpx", Type.Playlist, Type.VideoObject),
    AcquireMedia("acquiremedia", Type.NewsArticle),
    Workbench("workbench", Type.NewsArticle, Type.Taxonomy, Type.Slideshow, Type.Recipe),
    Newsvine("newsvine", Type.NewsArticle, Type.Taxonomy),
    FlowCMS("flowcms", Type.NewsArticle, Type.Taxonomy),
    MsnbcCMS("msnbc", Type.NewsArticle, Type.Slideshow, Type.Taxonomy, Type.Person, Type.Organization);

    public enum Type {

        NewsArticle("news-article"),
        Taxonomy("taxonomy"),
        VideoObject("video-object"),
        Recipe("recipe"),
        Slideshow("slideshow"),
        StaticPage("static-page"),
        Card("card"),
        Playlist("playlist"),
        Person("person"),
        Organization("organization");

        private final String elasticIndexType;

        Type(String elasticIndexType) {
            this.elasticIndexType = elasticIndexType;
        }

        @Override
        public String toString() {
            return this.elasticIndexType;
        }
    }

    private final String alias;
    private final Type[] types;

    private static final Map<String, Set<String>> typeToIndicesMap;
    private static final Map<String, Set<Type>> aliasToTypesMap;

    static {
        // For retrieving indices a given Type is indexed in
        Map<String, Set<String>> typeMap = new HashMap<>();
        Arrays.stream(values())
                .forEach(index -> Arrays.stream(index.types())
                        .forEach(type -> {
                            Set<String> indices = typeMap.get(type);
                            if (indices == null) {
                                indices = new HashSet<>();
                                typeMap.put(type, indices);
                            }
                            indices.add(index.alias());
                        }));
        typeToIndicesMap = Collections.unmodifiableMap(typeMap);

        // For retrieving Types indexed by a given index
        Map<String, Set<Type>> aliasMap = new HashMap<>();
        Arrays.stream(values())
                .forEach(index -> Arrays.stream(index.types)
                        .forEach(type -> {
                            Set<Type> typesIndexed = aliasMap.get(index.alias);
                            if (typesIndexed == null) {
                                typesIndexed = new HashSet<>();
                                aliasMap.put(index.alias, typesIndexed);
                            }
                            typesIndexed.add(type);
                        }));
        aliasToTypesMap = Collections.unmodifiableMap(aliasMap);
    }

    ElasticSearchIndices(String alias, Type... types) {
        this.alias = alias;
        this.types = types;
    }

    public String alias() {
        return alias;
    }

    public String externalSource() {
        return name();
    }

    public String[] types() {
        return Arrays.stream(types)
                .map(Type::toString)
                .toArray(String[]::new);
    }

    @Override
    public String toString() {
        return alias;
    }

    public static String[] indices() {
        return Arrays.stream(values())
                .map(ElasticSearchIndices::alias)
                .toArray(String[]::new);
    }

    public static String[] indices(Type... types) {
        return Arrays.stream(types)
                .flatMap(type -> typeToIndicesMap.get(type.toString()).stream())
                .toArray(String[]::new);
    }

    public static String[] indices(String... types) {
        return Arrays.stream(types)
                .flatMap(type -> typeToIndicesMap.get(type).stream())
                .toArray(String[]::new);
    }

    /**
     * Given an index alias, returns the Type documents indexed for that alias.
     */
    public static Set<Type> indexedTypesByPublisher(String indexAlias) {
        return aliasToTypesMap.get(indexAlias);
    }
}
