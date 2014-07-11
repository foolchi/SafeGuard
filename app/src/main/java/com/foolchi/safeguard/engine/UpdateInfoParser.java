package com.foolchi.safeguard.engine;

import java.io.InputStream;
import org.xmlpull.v1.XmlPullParser;
import android.util.Xml;
import com.foolchi.safeguard.domain.UpdateInfo;
/**
 * Created by foolchi on 6/20/14.
 */
public class UpdateInfoParser {

    public static UpdateInfo getUpdateInfo(InputStream is) throws Exception{
        UpdateInfo info = new UpdateInfo();
        XmlPullParser xmlPullParser = Xml.newPullParser();
        xmlPullParser.setInput(is, "utf-8");
        int type = xmlPullParser.getEventType();

        while(type != XmlPullParser.END_DOCUMENT){
            switch (type){
                case XmlPullParser.START_TAG:
                    if (xmlPullParser.getName().equals("version"))
                        info.setVersion(xmlPullParser.nextText());
                    else if (xmlPullParser.getName().equals("description"))
                        info.setDescription(xmlPullParser.nextText());
                    else if (xmlPullParser.getName().equals("apkurl"))
                        info.setUrl(xmlPullParser.nextText());
                    break;
                default:
                    break;
            }
            type = xmlPullParser.next();
        }
        System.out.println("Update Parser finished");
        return info;
    }
}
