package Utills;

import javax.mail.Message;

/**
 * Created by meirh on 25/04/2018.
 * See Example in "https://guides.codepath.com/android/Creating-Custom-Listeners"
 */

public class PersonalEvents
{
    public interface OnMessageLoaded
    {
        void onDataLoaded(Message[] messages);
    }

    public interface OnSongClick
    {
        void onSongPressed(int cardViewPressedResID, int listPositionIndex);
    }

    public interface OnListViewItemClick
    {
        void onListViewItemPressed(int listPositionIndex, String selectedItemText);
    }
}
