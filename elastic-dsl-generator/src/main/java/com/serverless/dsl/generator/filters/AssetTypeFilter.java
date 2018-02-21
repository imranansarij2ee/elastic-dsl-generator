package com.serverless.dsl.generator.filters;

public class AssetTypeFilter {
    private final String assetType;
    private final BitrateFilter bitrateFilter;

    public AssetTypeFilter(String assetType, BitrateFilter bitrateFilter) {
        this.assetType = assetType;
        this.bitrateFilter = bitrateFilter;
    }

    public String getAssetType() {
        return assetType;
    }

    public BitrateFilter getBitrateFilter() {
        return bitrateFilter;
    }

    public boolean matches(AvailableAsset asset) {
        boolean matchesAssetType = false;
        for (String at : asset.getAssetTypes()) {
            if (assetType.equalsIgnoreCase(at)) {
                matchesAssetType = true;
                break;
            }
        }
        return matchesAssetType && bitrateFilter.matches(asset.getBitrate());
    }
}
