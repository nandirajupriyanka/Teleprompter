package com.priyankanandiraju.teleprompter;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.priyankanandiraju.teleprompter.utils.TeleprompterFile;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.priyankanandiraju.teleprompter.utils.Constants.IMAGE_DATA;

/**
 * Created by priyankanandiraju on 8/8/17.
 */

class TeleprompterFilesAdapter extends RecyclerView.Adapter<TeleprompterFilesAdapter.TeleprompterFilesHolder> {

    private List<TeleprompterFile> mTeleprompterFileList;
    private OnFileClickListener mOnFileClickListener;

    public interface OnFileClickListener {
        void onFileClick(TeleprompterFile teleprompterFile);
        void onDownloadClick(TeleprompterFile teleprompterFile);
    }

    public TeleprompterFilesAdapter(List<TeleprompterFile> teleprompterFiles, OnFileClickListener onFileClickListener) {
        mTeleprompterFileList = teleprompterFiles;
        mOnFileClickListener = onFileClickListener;
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

        SharedPreferences shre = PreferenceManager.getDefaultSharedPreferences(holder.ivFileIcon.getContext());
        String previouslyEncodedImage = shre.getString(IMAGE_DATA + currentFileData.getTitle(), "");
        if (!previouslyEncodedImage.equalsIgnoreCase("")) {
            byte[] b = Base64.decode(previouslyEncodedImage, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
            holder.ivFileIcon.setImageBitmap(bitmap);
        } else {
            holder.ivFileIcon.setImageBitmap(null);
        }
    }

    @Override
    public int getItemCount() {
        return mTeleprompterFileList.size();
    }

    public void setFileData(List<TeleprompterFile> fileList) {
        mTeleprompterFileList.clear();
        mTeleprompterFileList.addAll(fileList);
        notifyDataSetChanged();
    }

    public class TeleprompterFilesHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.tv_title)
        TextView tvTitle;
        @BindView(R.id.tv_content)
        TextView tvContent;
        @BindView(R.id.iv_file_icon)
        ImageView ivFileIcon;
        @BindView(R.id.ib_download)
        ImageButton ibDownload;

        public TeleprompterFilesHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(this);
            ibDownload.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            TeleprompterFile teleprompterFile = mTeleprompterFileList.get(adapterPosition);
            switch (view.getId()) {
                case R.id.ib_download:
                    mOnFileClickListener.onDownloadClick(teleprompterFile);
                    break;
                default:
                    mOnFileClickListener.onFileClick(teleprompterFile);
                    break;
            }
        }
    }
}
