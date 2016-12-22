package unicommsapp.application.com.unicomppsapp.Utils;

import android.app.ProgressDialog;
import android.content.Context;

/**
 * Created by fahmed on 24.2.2016.
 */
public class UIUtils {

    public static ProgressDialog showDialogue( ProgressDialog dialogue, Context context){
        dialogue = new ProgressDialog(context);
        dialogue.setTitle("Unicomms App");
        dialogue.setMessage("Loading Please wait.....");
        dialogue.setCancelable(false);
        dialogue.setIndeterminate(true);
        dialogue.show();

        return dialogue;
    }

}
