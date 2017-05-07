package com.iemqra.bme.lostnfound;

import android.util.Base64;

import com.iemqra.bme.lostnfound.helper.EncryptHelper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

public class EncryptHelperTest {
    @Test
    public void saltGeneration() throws Exception {
        assertEquals(10, EncryptHelper.generateSalt().length());
    }


}
