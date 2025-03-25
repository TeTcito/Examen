package com.example.ojsmobileapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ojsmobileapp.R;
import com.example.ojsmobileapp.models.Issue;

import java.util.List;

public class IssueAdapter extends RecyclerView.Adapter<IssueAdapter.IssueViewHolder> {
    private Context context;
    private List<Issue> issues;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Issue issue);
    }

    public IssueAdapter(Context context, List<Issue> issues, OnItemClickListener listener) {
        this.context = context;
        this.issues = issues;
        this.listener = listener;
    }

    @NonNull
    @Override
    public IssueViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_issue, parent, false);
        return new IssueViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IssueViewHolder holder, int position) {
        Issue issue = issues.get(position);

        holder.title.setText(issue.getTitle());
        holder.volume.setText("Vol. " + issue.getVolume() + ", No. " + issue.getNumber());
        holder.year.setText(issue.getYear());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(issue);
            }
        });
    }

    @Override
    public int getItemCount() {
        return issues != null ? issues.size() : 0;
    }

    public static class IssueViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView volume;
        TextView year;

        public IssueViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.issueTitle);
            volume = itemView.findViewById(R.id.issueVolume);
            year = itemView.findViewById(R.id.issueYear);
        }
    }
}