package com.udacity.examples.Testing;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class HelperTest {

    @Test
    public void verify_getMergedList(){

        List<String> cadenas = Arrays.asList("Unir","estas","cadenas");
        String mergedList = Helper.getMergedList(cadenas);

        Assert.assertEquals("Unir, estas, cadenas",mergedList);
    }
    @Test
    public void verify_getFilteredList(){

        List<String> cadenas = Arrays.asList("Filtrar","","cadenas","");
        List<String> filteredList = Helper.getFilteredList(cadenas);
        List<String> filtro = Arrays.asList("Filtrar","cadenas");

        Assert.assertEquals(filtro,filteredList);
    }
}