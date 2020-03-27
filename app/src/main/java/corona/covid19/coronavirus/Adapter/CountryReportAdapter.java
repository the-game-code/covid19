package corona.covid19.coronavirus.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import corona.covid19.coronavirus.CountryReportActivity;
import corona.covid19.coronavirus.Model.CountryReportModel;
import corona.covid19.coronavirus.R;

public class CountryReportAdapter extends RecyclerView.Adapter<CountryReportAdapter.MyViewHolder> implements Filterable {

    private LayoutInflater inflater;
    public static List<CountryReportModel> itemList, itemList2;
    CountryReportActivity itmsObj;
    private CountryReportAdapter.ValueFilter valueFilter;

    public CountryReportAdapter(CountryReportActivity itmsObj, Context ctx, List<CountryReportModel> itemList) {
        inflater = LayoutInflater.from(ctx);
        this.itemList = itemList;
        this.itemList2 = itemList;
        this.itmsObj = itmsObj;
    }

    @Override
    public CountryReportAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.country_list, parent, false);
        CountryReportAdapter.MyViewHolder holder = new CountryReportAdapter.MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final CountryReportAdapter.MyViewHolder holder, final int position) {
        holder.countryName.setText(itemList.get(position).getCountry());
        holder.casesText.setText("Total cases: " + itemList.get(position).getCases());
        holder.deaths.setText("Total deaths: " + itemList.get(position).getDeaths());
        holder.todayCases.setText("Today's cases: " + itemList.get(position).getTodayCases());
        holder.todaysDeath.setText("Today's deaths: " + itemList.get(position).getTodayDeaths());
        holder.recovered.setText("Recovered: " + itemList.get(position).getRecovered());
        holder.active.setText("Active: " + itemList.get(position).getActive());
        holder.critical.setText("Critical: " + itemList.get(position).getCritical());
        holder.casesPerMillion.setText("Cases/Million: " + itemList.get(position).getCasesPerOneMillion());
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        protected TextView countryName, casesText, deaths, todayCases, todaysDeath, recovered, active, critical, casesPerMillion;

        public MyViewHolder(View itemView) {
            super(itemView);

            countryName = (TextView) itemView.findViewById(R.id.countryName);
            casesText = (TextView) itemView.findViewById(R.id.casesText);
            deaths = (TextView) itemView.findViewById(R.id.deaths);
            todayCases = (TextView) itemView.findViewById(R.id.todayCases);
            todaysDeath = (TextView) itemView.findViewById(R.id.todaysDeath);
            recovered = (TextView) itemView.findViewById(R.id.recovered);
            active = (TextView) itemView.findViewById(R.id.active);
            critical = (TextView) itemView.findViewById(R.id.critical);
            casesPerMillion = (TextView) itemView.findViewById(R.id.casesPerMillion);
        }

    }

    @Override
    public Filter getFilter() {
        if (valueFilter == null) {
            valueFilter = new CountryReportAdapter.ValueFilter();
        }
        return valueFilter;
    }


    private class ValueFilter extends Filter {

        //Invoked in a worker thread to filter the data according to the constraint.
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            constraint = constraint.toString().toLowerCase();
            FilterResults results = new FilterResults();
            if (constraint != null && constraint.length() > 0) {
                ArrayList<CountryReportModel> filterList = new ArrayList<CountryReportModel>();

                for (int i = 0; i < itemList2.size(); i++) {
                    CountryReportModel items = new CountryReportModel();
                    if ((itemList2.get(i).getCountry().toUpperCase()).contains(constraint.toString().toUpperCase()) || (itemList2.get(i).getCountry().toLowerCase()).contains(constraint.toString().toLowerCase())) {
                        items.setCountry(itemList2.get(i).getCountry());
                        items.setCases(itemList2.get(i).getCases());
                        items.setDeaths(itemList2.get(i).getDeaths());
                        items.setTodayCases(itemList2.get(i).getTodayCases());
                        items.setTodayDeaths(itemList2.get(i).getTodayDeaths());
                        items.setRecovered(itemList2.get(i).getRecovered());
                        items.setActive(itemList2.get(i).getActive());
                        items.setCritical(itemList2.get(i).getCritical());
                        items.setCasesPerOneMillion(itemList2.get(i).getCasesPerOneMillion());
                        filterList.add(items);
                    }
                }
                results.count = filterList.size();
                results.values = filterList;

            } else if (constraint.equals("")) {
                results.count = itemList.size();
                results.values = itemList;
            }
            return results;
        }

        //Invoked in the UI thread to publish the filtering results in the user interface.
        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            itemList = (ArrayList<CountryReportModel>) results.values;
            notifyDataSetChanged();
        }
    }
}
