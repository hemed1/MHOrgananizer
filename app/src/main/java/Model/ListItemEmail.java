package Model;

import android.widget.ImageView;

import java.util.Date;

/**
 * Created by meirh on 09/04/2018.
 */

public class ListItemEmail
{
    private String      Sender;
    private String      Subject;
    private String      Content;
    private String      DateSent;
    private String      DateReceive;
    private ImageView   ImageItem;


    public ListItemEmail(String sender, String subject)
    {
        this.Sender = sender;
        this.Subject = subject;
    }

    public String getDateSent() {
        return DateSent;
    }

    public void setDateSent(String dateSent) {
        DateSent = dateSent;
    }

    public String getDateReceive() {
        return DateReceive;
    }

    public void setDateReceive(String dateReceive) {
        DateReceive = dateReceive;
    }

    public ImageView getImgItem() {
        return ImageItem;
    }

    public void setImgItem(ImageView imgItem)
    {
        this.ImageItem = imgItem;
    }

    public String getSender()
    {
        return Sender;
    }

    public void setSender(String sender)
    {
        this.Sender = sender;
    }

    public String getSubject()
    {
        return Subject;
    }

    public void setSubject(String subject)
    {
        this.Subject = subject;
    }

    public String getContent()
    {
        return Content;
    }

    public void setContent(String content)
    {
        this.Content = content;
    }
}
