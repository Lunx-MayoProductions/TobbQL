package de.lunx.client;

import de.lunx.data.DataManager;

import static kong.unirest.Unirest.*;


/**
 * TODO: Update all the code to the actual query format! (Julian send ma auf süß paaer Beispiele)
 */

public class DefaultClient implements Client{
    String secrect;
    @Override
    public void execute(String tql) { //Format: action:value (ex: create_user:john)
        CharSequence[] split = tql.split(":");
        post(DataManager.getInstance().getConfiguration().getHostAddress()+":"+DataManager.getInstance().getConfiguration().getPort())
                .body("""
                        {
                            "ACTION":"VALUE"
                        }
                        """.replace("ACTION", split[0]).replace("VALUE", split[1]))
                .header("",""); //TODO: Authentication!
    }

    @Override
    public void token(String token) {
    secrect = token;
    }

    @Override
    public void customRequest(String body) {

    }
}
