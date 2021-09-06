package org.divulgit.remote.util;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class URLUtilTest {

    @Test
    public void testToListOfParamsWithOneValue() {
        List<String> ids = Arrays.asList("1");

        String listOfParams = URLUtil.toListOfParams(ids, "id[]");

        Assert.assertEquals("id[]=1", listOfParams);
    }

    @Test
    public void testToListOfParamsWithValues() {
        List<String> ids = Arrays.asList("1","2","3","4","5");

        String listOfParams = URLUtil.toListOfParams(ids, "id[]");

        Assert.assertEquals("id[]=1&id[]=2&id[]=3&id[]=4&id[]=5", listOfParams);
    }

    @Test
    public void testToListOfParamsWithoutValues() {
        List<String> ids = Collections.emptyList();

        String listOfParams = URLUtil.toListOfParams(ids, "id[]");

        Assert.assertEquals("", listOfParams);
    }

    @Test
    public void testPrepareToConcatBefore() {
        String params = "name=joao";

        String paramToConcat = URLUtil.prepareToConcat(params, true);

        Assert.assertEquals("&name=joao", paramToConcat);
    }

    @Test
    public void testPrepareToConcatAfter() {
        String params = "name=joao";

        String paramToConcat = URLUtil.prepareToConcat(params, false);

        Assert.assertEquals("name=joao&", paramToConcat);
    }

    @Test
    public void testPrepareToConcatNothing() {
        String params = "";

        String paramToConcat = URLUtil.prepareToConcat(params, false);

        Assert.assertEquals("", paramToConcat);
    }
}