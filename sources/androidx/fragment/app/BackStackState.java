package androidx.fragment.app;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.text.TextUtils;
import android.util.Log;
import androidx.lifecycle.Lifecycle.State;
import java.util.ArrayList;

@SuppressLint({"BanParcelableUsage"})
final class BackStackState implements Parcelable {
    public static final Creator<BackStackState> CREATOR = new Creator<BackStackState>() {
        public BackStackState createFromParcel(Parcel parcel) {
            return new BackStackState(parcel);
        }

        public BackStackState[] newArray(int i) {
            return new BackStackState[i];
        }
    };
    final int mBreadCrumbShortTitleRes;
    final CharSequence mBreadCrumbShortTitleText;
    final int mBreadCrumbTitleRes;
    final CharSequence mBreadCrumbTitleText;
    final int[] mCurrentMaxLifecycleStates;
    final ArrayList<String> mFragmentWhos;
    final int mIndex;
    final String mName;
    final int[] mOldMaxLifecycleStates;
    final int[] mOps;
    final boolean mReorderingAllowed;
    final ArrayList<String> mSharedElementSourceNames;
    final ArrayList<String> mSharedElementTargetNames;
    final int mTransition;

    public int describeContents() {
        return 0;
    }

    public BackStackState(BackStackRecord backStackRecord) {
        int size = backStackRecord.mOps.size();
        this.mOps = new int[(size * 5)];
        if (backStackRecord.mAddToBackStack) {
            this.mFragmentWhos = new ArrayList<>(size);
            this.mOldMaxLifecycleStates = new int[size];
            this.mCurrentMaxLifecycleStates = new int[size];
            int i = 0;
            int i2 = 0;
            while (i < size) {
                C0172Op op = (C0172Op) backStackRecord.mOps.get(i);
                int i3 = i2 + 1;
                this.mOps[i2] = op.mCmd;
                ArrayList<String> arrayList = this.mFragmentWhos;
                Fragment fragment = op.mFragment;
                arrayList.add(fragment != null ? fragment.mWho : null);
                int[] iArr = this.mOps;
                int i4 = i3 + 1;
                iArr[i3] = op.mEnterAnim;
                int i5 = i4 + 1;
                iArr[i4] = op.mExitAnim;
                int i6 = i5 + 1;
                iArr[i5] = op.mPopEnterAnim;
                int i7 = i6 + 1;
                iArr[i6] = op.mPopExitAnim;
                this.mOldMaxLifecycleStates[i] = op.mOldMaxState.ordinal();
                this.mCurrentMaxLifecycleStates[i] = op.mCurrentMaxState.ordinal();
                i++;
                i2 = i7;
            }
            this.mTransition = backStackRecord.mTransition;
            this.mName = backStackRecord.mName;
            this.mIndex = backStackRecord.mIndex;
            this.mBreadCrumbTitleRes = backStackRecord.mBreadCrumbTitleRes;
            this.mBreadCrumbTitleText = backStackRecord.mBreadCrumbTitleText;
            this.mBreadCrumbShortTitleRes = backStackRecord.mBreadCrumbShortTitleRes;
            this.mBreadCrumbShortTitleText = backStackRecord.mBreadCrumbShortTitleText;
            this.mSharedElementSourceNames = backStackRecord.mSharedElementSourceNames;
            this.mSharedElementTargetNames = backStackRecord.mSharedElementTargetNames;
            this.mReorderingAllowed = backStackRecord.mReorderingAllowed;
            return;
        }
        throw new IllegalStateException("Not on back stack");
    }

    public BackStackState(Parcel parcel) {
        this.mOps = parcel.createIntArray();
        this.mFragmentWhos = parcel.createStringArrayList();
        this.mOldMaxLifecycleStates = parcel.createIntArray();
        this.mCurrentMaxLifecycleStates = parcel.createIntArray();
        this.mTransition = parcel.readInt();
        this.mName = parcel.readString();
        this.mIndex = parcel.readInt();
        this.mBreadCrumbTitleRes = parcel.readInt();
        this.mBreadCrumbTitleText = (CharSequence) TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(parcel);
        this.mBreadCrumbShortTitleRes = parcel.readInt();
        this.mBreadCrumbShortTitleText = (CharSequence) TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(parcel);
        this.mSharedElementSourceNames = parcel.createStringArrayList();
        this.mSharedElementTargetNames = parcel.createStringArrayList();
        this.mReorderingAllowed = parcel.readInt() != 0;
    }

    public BackStackRecord instantiate(FragmentManager fragmentManager) {
        BackStackRecord backStackRecord = new BackStackRecord(fragmentManager);
        int i = 0;
        int i2 = 0;
        while (i < this.mOps.length) {
            C0172Op op = new C0172Op();
            int i3 = i + 1;
            op.mCmd = this.mOps[i];
            if (FragmentManager.isLoggingEnabled(2)) {
                StringBuilder sb = new StringBuilder();
                sb.append("Instantiate ");
                sb.append(backStackRecord);
                sb.append(" op #");
                sb.append(i2);
                sb.append(" base fragment #");
                sb.append(this.mOps[i3]);
                Log.v("FragmentManager", sb.toString());
            }
            String str = (String) this.mFragmentWhos.get(i2);
            if (str != null) {
                op.mFragment = fragmentManager.findActiveFragment(str);
            } else {
                op.mFragment = null;
            }
            op.mOldMaxState = State.values()[this.mOldMaxLifecycleStates[i2]];
            op.mCurrentMaxState = State.values()[this.mCurrentMaxLifecycleStates[i2]];
            int[] iArr = this.mOps;
            int i4 = i3 + 1;
            int i5 = iArr[i3];
            op.mEnterAnim = i5;
            int i6 = i4 + 1;
            int i7 = iArr[i4];
            op.mExitAnim = i7;
            int i8 = i6 + 1;
            int i9 = iArr[i6];
            op.mPopEnterAnim = i9;
            int i10 = i8 + 1;
            int i11 = iArr[i8];
            op.mPopExitAnim = i11;
            backStackRecord.mEnterAnim = i5;
            backStackRecord.mExitAnim = i7;
            backStackRecord.mPopEnterAnim = i9;
            backStackRecord.mPopExitAnim = i11;
            backStackRecord.addOp(op);
            i2++;
            i = i10;
        }
        backStackRecord.mTransition = this.mTransition;
        backStackRecord.mName = this.mName;
        backStackRecord.mIndex = this.mIndex;
        backStackRecord.mAddToBackStack = true;
        backStackRecord.mBreadCrumbTitleRes = this.mBreadCrumbTitleRes;
        backStackRecord.mBreadCrumbTitleText = this.mBreadCrumbTitleText;
        backStackRecord.mBreadCrumbShortTitleRes = this.mBreadCrumbShortTitleRes;
        backStackRecord.mBreadCrumbShortTitleText = this.mBreadCrumbShortTitleText;
        backStackRecord.mSharedElementSourceNames = this.mSharedElementSourceNames;
        backStackRecord.mSharedElementTargetNames = this.mSharedElementTargetNames;
        backStackRecord.mReorderingAllowed = this.mReorderingAllowed;
        backStackRecord.bumpBackStackNesting(1);
        return backStackRecord;
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeIntArray(this.mOps);
        parcel.writeStringList(this.mFragmentWhos);
        parcel.writeIntArray(this.mOldMaxLifecycleStates);
        parcel.writeIntArray(this.mCurrentMaxLifecycleStates);
        parcel.writeInt(this.mTransition);
        parcel.writeString(this.mName);
        parcel.writeInt(this.mIndex);
        parcel.writeInt(this.mBreadCrumbTitleRes);
        TextUtils.writeToParcel(this.mBreadCrumbTitleText, parcel, 0);
        parcel.writeInt(this.mBreadCrumbShortTitleRes);
        TextUtils.writeToParcel(this.mBreadCrumbShortTitleText, parcel, 0);
        parcel.writeStringList(this.mSharedElementSourceNames);
        parcel.writeStringList(this.mSharedElementTargetNames);
        parcel.writeInt(this.mReorderingAllowed ? 1 : 0);
    }
}
