package com.larryhsiao.nyx.jot;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.larryhsiao.nyx.core.jots.filter.ConstFilter;
import com.larryhsiao.nyx.core.jots.filter.Filter;
import org.junit.Assert;
import org.junit.Test;

/**
 * Unit-test for the class {@link FilterTypeAdapter}
 */
public class FilterTypeAdapterTest {

    /**
     * Test for serialize the fileter object.
     */
    @Test
    public void serialize() {
        Gson gson = new GsonBuilder()
            .registerTypeAdapter(Filter.class, new FilterTypeAdapter())
            .create();
        Assert.assertEquals(
            "{\"keyword\":\"keyword\",\"started\":1,\"ended\":2,\"ids\":[1,2,3]}",
            gson.toJson(
                new ConstFilter(new long[]{1, 2}, "keyword", new long[]{1, 2, 3}),
                new TypeToken<Filter>() {}.getType()
            )
        );
    }

    @Test
    public void deserialize() {
        Gson gson = new GsonBuilder()
            .registerTypeAdapter(Filter.class, new FilterTypeAdapter())
            .create();
        Filter filter = gson.fromJson(
            "{\"keyword\":\"keyword\",\"started\":1,\"ended\":2,\"ids\":[1,2,3]}",
            new TypeToken<Filter>() {}.getType()
        );
        Assert.assertArrayEquals(new long[]{1, 2, 3}, filter.ids());
        Assert.assertEquals("keyword", filter.keyword());
        Assert.assertArrayEquals(new long[]{1, 2}, filter.dateRange());
    }
}