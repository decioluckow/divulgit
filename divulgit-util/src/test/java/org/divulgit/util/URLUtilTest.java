package org.divulgit.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class URLUtilTest {

    @Test
    public void testToListOfParamsWithOneValue() {
        List<Integer> ids = Arrays.asList(1);

        String listOfParams = URLUtil.toListOfParams(ids, "id[]");

        Assertions.assertEquals("id[]=1", listOfParams);
    }

    @Test
    public void testToListOfParamsWithValues() {
        List<Integer> ids = Arrays.asList(1, 2, 3, 4, 5);

        String listOfParams = URLUtil.toListOfParams(ids, "id[]");

        Assertions.assertEquals("id[]=1&id[]=2&id[]=3&id[]=4&id[]=5", listOfParams);
    }

    @Test
    public void testToListOfParamsWithoutValues() {
        List<Integer> ids = Collections.emptyList();

        String listOfParams = URLUtil.toListOfParams(ids, "id[]");

        Assertions.assertEquals("", listOfParams);
    }

    @Test
    public void testPrepareToConcatBefore() {
        String params = "name=joao";

        String paramToConcat = URLUtil.prepareToConcat(params, true);

        Assertions.assertEquals("&name=joao", paramToConcat);
    }

    @Test
    public void testPrepareToConcatAfter() {
        String params = "name=joao";

        String paramToConcat = URLUtil.prepareToConcat(params, false);

        Assertions.assertEquals("name=joao&", paramToConcat);
    }

    @Test
    public void testPrepareToConcatNothing() {
        String params = "";

        String paramToConcat = URLUtil.prepareToConcat(params, false);

        Assertions.assertEquals("", paramToConcat);
    }
}