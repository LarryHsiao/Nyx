package com.larryhsiao.nyx.jot;

import android.database.MatrixCursor;
import androidx.appcompat.widget.SearchView;
import androidx.cursoradapter.widget.SimpleCursorAdapter;
import com.larryhsiao.nyx.core.tags.QueriedTags;
import com.larryhsiao.nyx.core.tags.Tag;
import com.larryhsiao.nyx.core.tags.TagsByKeyword;
import com.silverhetch.clotho.Action;
import com.silverhetch.clotho.Source;

import java.sql.Connection;
import java.util.List;

/**
 * Action to setup suggestions for given SearchView and input keyword.
 */
public class TagSuggestion implements Action {
    private final Source<Connection> db;
    private final String newText;
    private final SearchView searchView;

    public TagSuggestion(Source<Connection> db, String newText, SearchView searchView) {
        this.db = db;
        this.newText = newText;
        this.searchView = searchView;
    }

    @Override
    public void fire() {
        MatrixCursor cursor = new MatrixCursor(new String[]{"_id", "title"});
        List<Tag> tags = new QueriedTags(new TagsByKeyword(db, newText)).value();
        for (int i = 0; i < tags.size(); i++) {
            cursor.addRow(new String[]{String.valueOf(i), tags.get(i).title()});
        }
        searchView.setSuggestionsAdapter(new SimpleCursorAdapter(
            searchView.getContext(),
            android.R.layout.simple_list_item_1,
            cursor,
            new String[]{"title"},
            new int[]{android.R.id.text1},
            0
        ));

        searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionSelect(int position) {
                return false;
            }

            @Override
            public boolean onSuggestionClick(int position) {
                searchView.setQuery(tags.get(position).title(), true);
                return true;
            }
        });
    }
}
