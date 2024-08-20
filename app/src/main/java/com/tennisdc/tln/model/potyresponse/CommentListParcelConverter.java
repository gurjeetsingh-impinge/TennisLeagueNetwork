//package com.tennisdc.tln.model.potyresponse;
//
//import android.os.Parcel;
//
//import com.tennisdc.tln.model.Comment;
//import com.tennisdc.tln.model.RealmListParcelConverter;
//
//import org.parceler.Parcels;
//
//public class CommentListParcelConverter extends RealmListParcelConverter<Comment> {
//
//    @Override
//    public void itemToParcel(Comment input, Parcel parcel) {
//        parcel.writeParcelable(Parcels.wrap(input), 0);
//
//    }
//
//    @Override
//    public Comment itemFromParcel(Parcel parcel) {
//        return Parcels.unwrap(parcel.readParcelable(Comment.class.getClassLoader()));
//    }
//
//   /* @Override
//        public TrackingPoints itemFromParcel(android.os.Parcel parcel) {
//            return Parcels.unwrap(parcel.readParcelable(TrackingPoints.class.getClassLoader()));
//        }*/
//}
//
