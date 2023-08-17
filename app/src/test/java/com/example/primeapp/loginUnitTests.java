package com.example.primeapp;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;


import org.json.JSONObject;
import org.junit.Test;

public class loginUnitTests
{


    @Test
    public void parseResponse(){


            String json="{\"access_token\":\"0BERwQDJTqfEpI2oMBKlUj-z7DXG5uysZFeHbEzXxK7Sd-ZdS1_AiKfVfl7CVvCIEhJ-acmVdiH0d43oSK5oKhsG_e9oLTVRQIaIBWeWiqL7zeQzzVobZcRM--DZ3j5UioRvmvIr8QYNOGbWaf1D84oD6-Ol907oijaP7syBtKfJ09mVgQvH7dFrhDi5ClfWWqZ-1IgObVY_ZNRmVJafZBhumZokd5qd3a_xHtyBmoWaNQ-1s9q2F1OLR1XAo1nGHwzbmMlBd52jtuABEZQTf9zsPxlp0G_6ZE_HLGzAf9ddeyy-5e5R0Xo9XcpBhKUzPXv67mmm2QY98SSKxqMcusW09OLeVGm3zX-K9IsB4q5M1R1oYG8VTbt24-gSOe_dnLomTjAC06noo_J3Ss4dF4YWlzayTLEWHupkAI4eMpC_Gp8p0d6FxiSX3eHUNzvXKLX7KDxU2x4Zz-jxdZ-xP0qdmFJySVuc9To0jkWZhIiMObb6RIZTA6SCH4FevhtFzxZJT19VOELDZMbDGEBtrJgvQROsiylsNUaZU_yZeT8uMVAhL3Y2K7j6Fonim7KlFy9v4NNw0M1k3BTiQ_qWPUOU3qS0FHoUf2qAoBAmJc0yr11B9ILLdK9LyW4x4gwhT97FBfuvzLcYD9_zf939Qsp-tMUBxmlAQ_-0BKFqYAcAURatSTz5x95Sxa2lB9TfgbfDZ19TZplOhWW89lM3uQ\",\"token_type\":\"bearer\",\"expires_in\":1209599,\"userName\":\"ΚΑΝΕΛΛΟΣ ΦΟΙΒΟΣ\",\"roleName\":\"Doctor\",\"id\":\"sdfsdfds1368\",\"businessStart\":\"540\",\"businessEnd\":\"1260\",\".issued\":\"Mon, 08 May 2023 16:55:41 GMT\",\".expires\":\"Mon, 22 May 2023 16:55:41 GMT\"}";

            try {
                JSONObject object = new JSONObject(json);
                assertTrue(true);
            }
            catch (Exception ex){

                fail();
            }


    }
}