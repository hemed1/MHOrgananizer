package Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
//import com.gohool.meirh.recyclerviewapp.DetailsActivity;
import com.example.meirh.mhorgananizer.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import Model.ListItemEmail;
import Utills.PersonalEvents;

/**
 * Created by meirh on 09/04/2018.
 */

public class AdapterEmail extends RecyclerView.Adapter<AdapterEmail.EmailHolder>
{
    private Context context;
    private List<ListItemEmail> listItems;

    // The listener must implement the events interface and passes messages up to the parent.
    private PersonalEvents.OnRecyclerViewItemClick      listener;

    public AdapterEmail(Context context, List listItems)
    {
        this.context = context;
        this.listItems = listItems;
    }

    // Assign the listener implementing events interface that will receive the events
    public void setOnSongClick(PersonalEvents.OnRecyclerViewItemClick  listener)
    {
        this.listener = listener;
    }

    @Override
    public AdapterEmail.EmailHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View  cardViewItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_email, parent, false);
        EmailHolder emailHolder = new EmailHolder(cardViewItem);

        return emailHolder;
    }

    @Override
    public void onBindViewHolder(AdapterEmail.EmailHolder holder, int position)
    {
        ListItemEmail item = listItems.get(position);

        holder.lblSender.setText(item.getSender());
        holder.lblSubject.setText(item.getSubject());
        holder.lblContent.setText(item.getContent());
        holder.lblDateSent.setText(item.getDateSent());
        holder.lblDateSent.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
        //TODO: holder.lblDateReceive.setText(item.getDateReceive());
        holder.Image.setImageDrawable(item.getImgItem().getDrawable());
        //holder.Image.setBackground(item.getImgItem().getDrawable());
    }

    @Override
    public int getItemCount()
    {
        return listItems.size();
    }

    public class EmailHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        private TextView  lblSender;
        private TextView  lblSubject;
        private TextView  lblContent;
        private TextView  lblDateSent;
        private TextView  lblDateReceive;
        private ImageView Image;

        public EmailHolder(View cardViewItem)   // Represent the Item in List
        {
            super(cardViewItem);

            cardViewItem.setOnClickListener(this);

            lblSender = (TextView) cardViewItem.findViewById(R.id.lblSender);
            lblSubject = (TextView) cardViewItem.findViewById(R.id.lblSubject);
            lblContent = (TextView) cardViewItem.findViewById(R.id.lblContent);
            lblDateSent = (TextView) cardViewItem.findViewById(R.id.lblDateSent);
            lblDateSent.setVisibility(View.VISIBLE);
            //TODO: lblDateReceive = (TextView) cardViewItem.findViewById(R.id.lblDateSent);
            Image = (ImageView) cardViewItem.findViewById(R.id.imgItem);
        }

        @Override
        public void onClick(View view)
        {
            ListItemEmail item = listItems.get(getAdapterPosition());

            Toast.makeText(context, item.getSender(), Toast.LENGTH_SHORT).show();

            //TODO: Open mail details view

//            if (listener != null)
//            {
//                // Now let's fire listener here
//                listener.setOnRecyclerViewItemPressed(item.getResourceID(), getAdapterPosition());
//            }

//            Intent intent = new Intent(context, DetailsActivity.class);
//            intent.putExtra("Name", item.getName());
//            intent.putExtra("Description", item.getDescription());
//            intent.putExtra("Rating", item.getRating());
//
//            context.startActivity(intent);

        }
    }
}
