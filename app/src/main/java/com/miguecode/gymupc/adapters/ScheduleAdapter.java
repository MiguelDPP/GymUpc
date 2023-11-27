package com.miguecode.gymupc.adapters;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.miguecode.gymupc.DetailScheduleActivity;
import com.miguecode.gymupc.models.Schedule;
import com.miguecode.gymupc.R;
import com.miguecode.gymupc.utils.Dates;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ViewHolder> {

    private ArrayList<Schedule> localDataSet;
    private ArrayList<Schedule> localDatasetOriginal;

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageStatus;
        public TextView txtFecha;
        public TextView txtHora;

        Schedule schedule;


        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View
            view.setOnClickListener((v) -> {

                Bundle bundle = new Bundle();
                //Pasar el objeto contacto
                bundle.putSerializable("schedule", this.schedule);

                Intent detailScheduleActivity = new Intent(v.getContext(), DetailScheduleActivity.class);
                detailScheduleActivity.putExtras(bundle);
                v.getContext().startActivity(detailScheduleActivity);

            });

            imageStatus = view.findViewById(R.id.imageStatus);
            txtFecha = view.findViewById(R.id.txtFecha);
            txtHora = view.findViewById(R.id.txtHora);
        }

    }

    /**
     * Initialize the dataset of the Adapter.
     *
     * @param dataSet String[] containing the data to populate views to be used
     * by RecyclerView.
     */
    public ScheduleAdapter(ArrayList<Schedule> dataSet) {
        this.localDataSet = dataSet;
        this.localDatasetOriginal = new ArrayList<>();
        this.localDatasetOriginal.addAll(dataSet);
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_schedule, viewGroup, false);

        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        viewHolder.schedule = localDataSet.get(position);
        Date date = Dates.convertTODate(viewHolder.schedule.getDate());
        //cONVERTIR A Time una hora
        date.setHours(Integer.parseInt(viewHolder.schedule.getHour().split(":")[0]));
        Date today = new Date();
        if (viewHolder.schedule.isWent()) {
            viewHolder.imageStatus.setImageResource(R.drawable.check_md_icon);
            //Cambiar color del icono
            viewHolder.imageStatus.setColorFilter(viewHolder.imageStatus.getContext().getResources().getColor(R.color.green));
        } else if (Dates.esMayorConHoras(today, date) && !viewHolder.schedule.isWent()) {
            viewHolder.imageStatus.setImageResource(R.drawable.cancel_md_icon);
            //Cambiar color del icono a @android:color/holo_red_light
            viewHolder.imageStatus.setColorFilter(viewHolder.imageStatus.getContext().getResources().getColor(R.color.holo_red_light));
        } else {
            viewHolder.imageStatus.setImageResource(R.drawable.clock_md_icon);
            //Cambiar color del icono
            viewHolder.imageStatus.setColorFilter(viewHolder.imageStatus.getContext().getResources().getColor(R.color.gray_bg_input));
        }
        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.txtFecha.setText(Dates.toDateStringLong(Dates.convertTODate(viewHolder.schedule.getDate())));
        viewHolder.txtHora.setText(viewHolder.schedule.getHour());
    }

    public void filtrado(String texto){
        localDataSet.clear();
        if(texto.length() == 0){
            localDataSet.addAll(localDatasetOriginal);
        }else{
            List<Schedule> collecion = localDatasetOriginal.stream().filter(i -> {
                String fecha = Dates.toDateStringLong(Dates.convertTODate(i.getDate()));
                return fecha.toLowerCase().contains(texto.toLowerCase()) || i.getHour().toLowerCase().contains(texto.toLowerCase()) || i.getDate().toLowerCase().contains(texto.toLowerCase());
            }).collect(Collectors.toList());
            localDataSet.addAll(collecion);
        }
        notifyDataSetChanged();
    }



    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return localDataSet.size();
    }
}
