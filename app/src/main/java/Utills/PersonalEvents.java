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

    public interface OnRecyclerViewItemClick
    {
        void setOnRecyclerViewItemPressed(int cardViewPressedResID, int listPositionIndex);
    }

    public interface OnListViewItemClick
    {
        void setOnListViewItemPressed(int listPositionIndex, String selectedItemText);
    }
}
