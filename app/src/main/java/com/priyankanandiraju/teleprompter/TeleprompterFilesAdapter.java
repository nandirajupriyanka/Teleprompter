package com.priyankanandiraju.teleprompter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.priyankanandiraju.teleprompter.utils.TeleprompterFile;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by priyankanandiraju on 8/8/17.
 */

class TeleprompterFilesAdapter extends RecyclerView.Adapter<TeleprompterFilesAdapter.TeleprompterFilesHolder> {

    private List<TeleprompterFile> mTeleprompterFileList;

    public TeleprompterFilesAdapter(List<TeleprompterFile> teleprompterFiles) {
        mTeleprompterFileList = teleprompterFiles;
    }

    @Override
    public TeleprompterFilesHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_teleprompter_file, parent, false);
        return new TeleprompterFilesHolder(view);
    }

    @Override
    public void onBindViewHolder(TeleprompterFilesHolder holder, int position) {
        TeleprompterFile currentFileData = mTeleprompterFileList.get(position);
        holder.tvTitle.setText(currentFileData.getTitle());
        holder.tvContent.setText(currentFileData.getContent());
    }

    @Override
    public int getItemCount() {
        return mTeleprompterFileList.size();
    }

    public void setFileData(List<TeleprompterFile> fileList) {
        mTeleprompterFileList.clear();
        mTeleprompterFileList.addAll(fileList);
    }

    public class TeleprompterFilesHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_title)
        TextView tvTitle;
        @BindView(R.id.tv_content)
        TextView tvContent;
        public TeleprompterFilesHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
