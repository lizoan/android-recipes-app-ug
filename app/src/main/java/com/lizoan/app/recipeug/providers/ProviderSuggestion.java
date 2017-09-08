package com.lizoan.app.recipeug.providers;

import android.content.SearchRecentSuggestionsProvider;

public class ProviderSuggestion extends SearchRecentSuggestionsProvider {
    public final static String AUTHORITY = "com.lizoan.app.recipeug.providers.ProviderSuggestion";
    public final static int MODE = DATABASE_MODE_QUERIES;

    public ProviderSuggestion(){
        setupSuggestions(AUTHORITY, MODE);
    }

}
