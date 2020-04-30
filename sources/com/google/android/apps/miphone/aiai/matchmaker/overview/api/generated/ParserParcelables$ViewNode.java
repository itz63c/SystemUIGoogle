package com.google.android.apps.miphone.aiai.matchmaker.overview.api.generated;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.util.Arrays;
import java.util.List;

public final class ParserParcelables$ViewNode implements Parcelable {
    public static final Creator<ParserParcelables$ViewNode> CREATOR = new Creator<ParserParcelables$ViewNode>() {
        public ParserParcelables$ViewNode createFromParcel(Parcel parcel) {
            return new ParserParcelables$ViewNode(parcel);
        }

        public ParserParcelables$ViewNode[] newArray(int i) {
            return new ParserParcelables$ViewNode[i];
        }
    };
    public List<String> autofillHints;
    public int autofillId;
    public String autofillIdStr;
    public List<String> autofillOptions;
    public int autofillType;
    public String autofillValue;
    public List<ParserParcelables$ViewNode> children;
    public String className;
    public String contentDescription;
    public int height;
    public String hint;

    /* renamed from: id */
    public int f95id;
    public String idEntry;
    public int inputType;
    public boolean isAccessibilityFocused;
    public boolean isActivated;
    public boolean isAssistBlocked;
    public boolean isCheckable;
    public boolean isChecked;
    public boolean isClickable;
    public boolean isContextClickable;
    public boolean isEnabled;
    public boolean isFocusable;
    public boolean isFocused;
    public boolean isLongClickable;
    public boolean isOpaque;
    public boolean isSelected;
    public int left;
    public int leftWrtParent;
    public String localeList;
    public int maxTextEms;
    public int maxTextLength;
    public int minTextEms;
    public int resourceId;
    public int scrollX;
    public int scrollY;
    public String text;
    public int textBackgroundColor;
    public int textColor;
    public String textIdEntry;
    public List<Integer> textLineBaseLines;
    public List<Integer> textLineCharOffsets;
    public int textSelectionEnd;
    public int textSelectionStart;
    public float textSize;
    public int textStyle;
    public int top;
    public int topWrtParent;
    public int visibility;
    public int width;

    public int describeContents() {
        return 0;
    }

    public ParserParcelables$ViewNode(Parcel parcel) {
        readFromParcel(parcel);
    }

    private ParserParcelables$ViewNode() {
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.f95id);
        if (this.children == null) {
            parcel.writeByte(0);
        } else {
            parcel.writeByte(1);
            parcel.writeInt(this.children.size());
            for (ParserParcelables$ViewNode parserParcelables$ViewNode : this.children) {
                if (parserParcelables$ViewNode == null) {
                    parcel.writeByte(0);
                } else {
                    parcel.writeByte(1);
                    parserParcelables$ViewNode.writeToParcel(parcel, i);
                }
            }
        }
        parcel.writeInt(this.left);
        parcel.writeInt(this.top);
        parcel.writeInt(this.width);
        parcel.writeInt(this.height);
        parcel.writeInt(this.visibility);
        if (this.className == null) {
            parcel.writeByte(0);
        } else {
            parcel.writeByte(1);
            parcel.writeString(this.className);
        }
        if (this.text == null) {
            parcel.writeByte(0);
        } else {
            parcel.writeByte(1);
            parcel.writeString(this.text);
        }
        if (this.idEntry == null) {
            parcel.writeByte(0);
        } else {
            parcel.writeByte(1);
            parcel.writeString(this.idEntry);
        }
        parcel.writeInt(this.leftWrtParent);
        parcel.writeInt(this.topWrtParent);
        parcel.writeInt(this.autofillId);
        if (this.contentDescription == null) {
            parcel.writeByte(0);
        } else {
            parcel.writeByte(1);
            parcel.writeString(this.contentDescription);
        }
        parcel.writeInt(this.resourceId);
        parcel.writeInt(this.scrollX);
        parcel.writeInt(this.scrollY);
        if (this.isAssistBlocked) {
            parcel.writeByte(1);
        } else {
            parcel.writeByte(0);
        }
        if (this.isEnabled) {
            parcel.writeByte(1);
        } else {
            parcel.writeByte(0);
        }
        if (this.isClickable) {
            parcel.writeByte(1);
        } else {
            parcel.writeByte(0);
        }
        if (this.isLongClickable) {
            parcel.writeByte(1);
        } else {
            parcel.writeByte(0);
        }
        if (this.isContextClickable) {
            parcel.writeByte(1);
        } else {
            parcel.writeByte(0);
        }
        if (this.isFocusable) {
            parcel.writeByte(1);
        } else {
            parcel.writeByte(0);
        }
        if (this.isFocused) {
            parcel.writeByte(1);
        } else {
            parcel.writeByte(0);
        }
        if (this.isAccessibilityFocused) {
            parcel.writeByte(1);
        } else {
            parcel.writeByte(0);
        }
        if (this.isCheckable) {
            parcel.writeByte(1);
        } else {
            parcel.writeByte(0);
        }
        if (this.isChecked) {
            parcel.writeByte(1);
        } else {
            parcel.writeByte(0);
        }
        if (this.isSelected) {
            parcel.writeByte(1);
        } else {
            parcel.writeByte(0);
        }
        if (this.isActivated) {
            parcel.writeByte(1);
        } else {
            parcel.writeByte(0);
        }
        if (this.isOpaque) {
            parcel.writeByte(1);
        } else {
            parcel.writeByte(0);
        }
        if (this.hint == null) {
            parcel.writeByte(0);
        } else {
            parcel.writeByte(1);
            parcel.writeString(this.hint);
        }
        parcel.writeInt(this.textSelectionStart);
        parcel.writeInt(this.textSelectionEnd);
        parcel.writeInt(this.textColor);
        parcel.writeInt(this.textBackgroundColor);
        parcel.writeFloat(this.textSize);
        parcel.writeInt(this.textStyle);
        if (this.textLineCharOffsets == null) {
            parcel.writeByte(0);
        } else {
            parcel.writeByte(1);
            parcel.writeInt(this.textLineCharOffsets.size());
            for (Integer num : this.textLineCharOffsets) {
                if (num == null) {
                    parcel.writeByte(0);
                } else {
                    parcel.writeByte(1);
                    parcel.writeInt(num.intValue());
                }
            }
        }
        if (this.textLineBaseLines == null) {
            parcel.writeByte(0);
        } else {
            parcel.writeByte(1);
            parcel.writeInt(this.textLineBaseLines.size());
            for (Integer num2 : this.textLineBaseLines) {
                if (num2 == null) {
                    parcel.writeByte(0);
                } else {
                    parcel.writeByte(1);
                    parcel.writeInt(num2.intValue());
                }
            }
        }
        parcel.writeInt(this.inputType);
        parcel.writeInt(this.minTextEms);
        parcel.writeInt(this.maxTextEms);
        parcel.writeInt(this.maxTextLength);
        if (this.textIdEntry == null) {
            parcel.writeByte(0);
        } else {
            parcel.writeByte(1);
            parcel.writeString(this.textIdEntry);
        }
        parcel.writeInt(this.autofillType);
        if (this.autofillHints == null) {
            parcel.writeByte(0);
        } else {
            parcel.writeByte(1);
            parcel.writeInt(this.autofillHints.size());
            for (String str : this.autofillHints) {
                if (str == null) {
                    parcel.writeByte(0);
                } else {
                    parcel.writeByte(1);
                    parcel.writeString(str);
                }
            }
        }
        if (this.autofillValue == null) {
            parcel.writeByte(0);
        } else {
            parcel.writeByte(1);
            parcel.writeString(this.autofillValue);
        }
        if (this.autofillOptions == null) {
            parcel.writeByte(0);
        } else {
            parcel.writeByte(1);
            parcel.writeInt(this.autofillOptions.size());
            for (String str2 : this.autofillOptions) {
                if (str2 == null) {
                    parcel.writeByte(0);
                } else {
                    parcel.writeByte(1);
                    parcel.writeString(str2);
                }
            }
        }
        if (this.localeList == null) {
            parcel.writeByte(0);
        } else {
            parcel.writeByte(1);
            parcel.writeString(this.localeList);
        }
        if (this.autofillIdStr == null) {
            parcel.writeByte(0);
            return;
        }
        parcel.writeByte(1);
        parcel.writeString(this.autofillIdStr);
    }

    private void readFromParcel(Parcel parcel) {
        String str;
        String str2;
        Integer num;
        Integer num2;
        ParserParcelables$ViewNode parserParcelables$ViewNode;
        this.f95id = parcel.readInt();
        if (parcel.readByte() == 0) {
            this.children = null;
        } else {
            int readInt = parcel.readInt();
            ParserParcelables$ViewNode[] parserParcelables$ViewNodeArr = new ParserParcelables$ViewNode[readInt];
            for (int i = 0; i < readInt; i++) {
                if (parcel.readByte() == 0) {
                    parserParcelables$ViewNode = null;
                } else {
                    parserParcelables$ViewNode = (ParserParcelables$ViewNode) CREATOR.createFromParcel(parcel);
                }
                parserParcelables$ViewNodeArr[i] = parserParcelables$ViewNode;
            }
            this.children = Arrays.asList(parserParcelables$ViewNodeArr);
        }
        this.left = parcel.readInt();
        this.top = parcel.readInt();
        this.width = parcel.readInt();
        this.height = parcel.readInt();
        this.visibility = parcel.readInt();
        if (parcel.readByte() == 0) {
            this.className = null;
        } else {
            this.className = parcel.readString();
        }
        if (parcel.readByte() == 0) {
            this.text = null;
        } else {
            this.text = parcel.readString();
        }
        if (parcel.readByte() == 0) {
            this.idEntry = null;
        } else {
            this.idEntry = parcel.readString();
        }
        this.leftWrtParent = parcel.readInt();
        this.topWrtParent = parcel.readInt();
        this.autofillId = parcel.readInt();
        if (parcel.readByte() == 0) {
            this.contentDescription = null;
        } else {
            this.contentDescription = parcel.readString();
        }
        this.resourceId = parcel.readInt();
        this.scrollX = parcel.readInt();
        this.scrollY = parcel.readInt();
        boolean z = true;
        this.isAssistBlocked = parcel.readByte() == 1;
        this.isEnabled = parcel.readByte() == 1;
        this.isClickable = parcel.readByte() == 1;
        this.isLongClickable = parcel.readByte() == 1;
        this.isContextClickable = parcel.readByte() == 1;
        this.isFocusable = parcel.readByte() == 1;
        this.isFocused = parcel.readByte() == 1;
        this.isAccessibilityFocused = parcel.readByte() == 1;
        this.isCheckable = parcel.readByte() == 1;
        this.isChecked = parcel.readByte() == 1;
        this.isSelected = parcel.readByte() == 1;
        this.isActivated = parcel.readByte() == 1;
        if (parcel.readByte() != 1) {
            z = false;
        }
        this.isOpaque = z;
        if (parcel.readByte() == 0) {
            this.hint = null;
        } else {
            this.hint = parcel.readString();
        }
        this.textSelectionStart = parcel.readInt();
        this.textSelectionEnd = parcel.readInt();
        this.textColor = parcel.readInt();
        this.textBackgroundColor = parcel.readInt();
        this.textSize = parcel.readFloat();
        this.textStyle = parcel.readInt();
        if (parcel.readByte() == 0) {
            this.textLineCharOffsets = null;
        } else {
            int readInt2 = parcel.readInt();
            Integer[] numArr = new Integer[readInt2];
            for (int i2 = 0; i2 < readInt2; i2++) {
                if (parcel.readByte() == 0) {
                    num2 = null;
                } else {
                    num2 = Integer.valueOf(parcel.readInt());
                }
                numArr[i2] = num2;
            }
            this.textLineCharOffsets = Arrays.asList(numArr);
        }
        if (parcel.readByte() == 0) {
            this.textLineBaseLines = null;
        } else {
            int readInt3 = parcel.readInt();
            Integer[] numArr2 = new Integer[readInt3];
            for (int i3 = 0; i3 < readInt3; i3++) {
                if (parcel.readByte() == 0) {
                    num = null;
                } else {
                    num = Integer.valueOf(parcel.readInt());
                }
                numArr2[i3] = num;
            }
            this.textLineBaseLines = Arrays.asList(numArr2);
        }
        this.inputType = parcel.readInt();
        this.minTextEms = parcel.readInt();
        this.maxTextEms = parcel.readInt();
        this.maxTextLength = parcel.readInt();
        if (parcel.readByte() == 0) {
            this.textIdEntry = null;
        } else {
            this.textIdEntry = parcel.readString();
        }
        this.autofillType = parcel.readInt();
        if (parcel.readByte() == 0) {
            this.autofillHints = null;
        } else {
            int readInt4 = parcel.readInt();
            String[] strArr = new String[readInt4];
            for (int i4 = 0; i4 < readInt4; i4++) {
                if (parcel.readByte() == 0) {
                    str2 = null;
                } else {
                    str2 = parcel.readString();
                }
                strArr[i4] = str2;
            }
            this.autofillHints = Arrays.asList(strArr);
        }
        if (parcel.readByte() == 0) {
            this.autofillValue = null;
        } else {
            this.autofillValue = parcel.readString();
        }
        if (parcel.readByte() == 0) {
            this.autofillOptions = null;
        } else {
            int readInt5 = parcel.readInt();
            String[] strArr2 = new String[readInt5];
            for (int i5 = 0; i5 < readInt5; i5++) {
                if (parcel.readByte() == 0) {
                    str = null;
                } else {
                    str = parcel.readString();
                }
                strArr2[i5] = str;
            }
            this.autofillOptions = Arrays.asList(strArr2);
        }
        if (parcel.readByte() == 0) {
            this.localeList = null;
        } else {
            this.localeList = parcel.readString();
        }
        if (parcel.readByte() == 0) {
            this.autofillIdStr = null;
        } else {
            this.autofillIdStr = parcel.readString();
        }
    }
}
