package com.smagar.sisimangi.api;

import static com.smagar.sisimangi.util.Utility.BASE_URL_API;

/**
 * Created by Gustiawan on 1/20/2019.
 */

public class Server {
    public static ApiService getAPIService() {
        return Client.getClient(BASE_URL_API).create(ApiService.class);
    }
}
