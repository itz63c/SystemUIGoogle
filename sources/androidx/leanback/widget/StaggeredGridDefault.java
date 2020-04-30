package androidx.leanback.widget;

import androidx.leanback.widget.StaggeredGrid.Location;

final class StaggeredGridDefault extends StaggeredGrid {
    StaggeredGridDefault() {
    }

    /* access modifiers changed from: 0000 */
    public int getRowMax(int i) {
        int i2;
        Location location;
        int i3 = this.mFirstVisibleIndex;
        if (i3 < 0) {
            return Integer.MIN_VALUE;
        }
        if (this.mReversedFlow) {
            int edge = this.mProvider.getEdge(i3);
            if (getLocation(this.mFirstVisibleIndex).row == i) {
                return edge;
            }
            int i4 = this.mFirstVisibleIndex;
            do {
                i4++;
                if (i4 <= getLastIndex()) {
                    location = getLocation(i4);
                    edge += location.offset;
                }
            } while (location.row != i);
            return edge;
        }
        int edge2 = this.mProvider.getEdge(this.mLastVisibleIndex);
        Location location2 = getLocation(this.mLastVisibleIndex);
        if (location2.row != i) {
            int i5 = this.mLastVisibleIndex;
            while (true) {
                i5--;
                if (i5 < getFirstIndex()) {
                    break;
                }
                edge2 -= location2.offset;
                location2 = getLocation(i5);
                if (location2.row == i) {
                    i2 = location2.size;
                    break;
                }
            }
        } else {
            i2 = location2.size;
        }
        return edge2 + i2;
        return Integer.MIN_VALUE;
    }

    /* access modifiers changed from: 0000 */
    public int getRowMin(int i) {
        Location location;
        int i2;
        int i3 = this.mFirstVisibleIndex;
        if (i3 < 0) {
            return Integer.MAX_VALUE;
        }
        if (this.mReversedFlow) {
            int edge = this.mProvider.getEdge(this.mLastVisibleIndex);
            Location location2 = getLocation(this.mLastVisibleIndex);
            if (location2.row != i) {
                int i4 = this.mLastVisibleIndex;
                while (true) {
                    i4--;
                    if (i4 < getFirstIndex()) {
                        break;
                    }
                    edge -= location2.offset;
                    location2 = getLocation(i4);
                    if (location2.row == i) {
                        i2 = location2.size;
                        break;
                    }
                }
            } else {
                i2 = location2.size;
            }
            return edge - i2;
        }
        int edge2 = this.mProvider.getEdge(i3);
        if (getLocation(this.mFirstVisibleIndex).row == i) {
            return edge2;
        }
        int i5 = this.mFirstVisibleIndex;
        do {
            i5++;
            if (i5 <= getLastIndex()) {
                location = getLocation(i5);
                edge2 += location.offset;
            }
        } while (location.row != i);
        return edge2;
        return Integer.MAX_VALUE;
    }

    public int findRowMax(boolean z, int i, int[] iArr) {
        int i2;
        int edge = this.mProvider.getEdge(i);
        Location location = getLocation(i);
        int i3 = location.row;
        if (this.mReversedFlow) {
            i2 = i3;
            int i4 = i2;
            int i5 = 1;
            int i6 = i + 1;
            int i7 = edge;
            while (i5 < this.mNumRows && i6 <= this.mLastVisibleIndex) {
                Location location2 = getLocation(i6);
                i7 += location2.offset;
                int i8 = location2.row;
                if (i8 != i4) {
                    i5++;
                    if (!z ? i7 >= edge : i7 <= edge) {
                        i4 = i8;
                    } else {
                        edge = i7;
                        i = i6;
                        i2 = i8;
                        i4 = i2;
                    }
                }
                i6++;
            }
        } else {
            int i9 = 1;
            int i10 = i - 1;
            int i11 = i3;
            Location location3 = location;
            int i12 = edge;
            edge = this.mProvider.getSize(i) + edge;
            i2 = i11;
            while (i9 < this.mNumRows && i10 >= this.mFirstVisibleIndex) {
                i12 -= location3.offset;
                location3 = getLocation(i10);
                int i13 = location3.row;
                if (i13 != i11) {
                    i9++;
                    int size = this.mProvider.getSize(i10) + i12;
                    if (!z ? size >= edge : size <= edge) {
                        i11 = i13;
                    } else {
                        edge = size;
                        i = i10;
                        i2 = i13;
                        i11 = i2;
                    }
                }
                i10--;
            }
        }
        if (iArr != null) {
            iArr[0] = i2;
            iArr[1] = i;
        }
        return edge;
    }

    public int findRowMin(boolean z, int i, int[] iArr) {
        int i2;
        int edge = this.mProvider.getEdge(i);
        Location location = getLocation(i);
        int i3 = location.row;
        if (this.mReversedFlow) {
            int i4 = 1;
            int i5 = i - 1;
            i2 = edge - this.mProvider.getSize(i);
            int i6 = i3;
            while (i4 < this.mNumRows && i5 >= this.mFirstVisibleIndex) {
                edge -= location.offset;
                location = getLocation(i5);
                int i7 = location.row;
                if (i7 != i6) {
                    i4++;
                    int size = edge - this.mProvider.getSize(i5);
                    if (!z ? size >= i2 : size <= i2) {
                        i6 = i7;
                    } else {
                        i2 = size;
                        i = i5;
                        i3 = i7;
                        i6 = i3;
                    }
                }
                i5--;
            }
        } else {
            int i8 = i3;
            int i9 = i8;
            int i10 = 1;
            int i11 = i + 1;
            int i12 = edge;
            while (i10 < this.mNumRows && i11 <= this.mLastVisibleIndex) {
                Location location2 = getLocation(i11);
                i12 += location2.offset;
                int i13 = location2.row;
                if (i13 != i9) {
                    i10++;
                    if (!z ? i12 >= edge : i12 <= edge) {
                        i9 = i13;
                    } else {
                        edge = i12;
                        i = i11;
                        i8 = i13;
                        i9 = i8;
                    }
                }
                i11++;
            }
            i2 = edge;
            i3 = i8;
        }
        if (iArr != null) {
            iArr[0] = i3;
            iArr[1] = i;
        }
        return i2;
    }

    private int findRowEdgeLimitSearchIndex(boolean z) {
        boolean z2 = false;
        if (z) {
            for (int i = this.mLastVisibleIndex; i >= this.mFirstVisibleIndex; i--) {
                int i2 = getLocation(i).row;
                if (i2 == 0) {
                    z2 = true;
                } else if (z2 && i2 == this.mNumRows - 1) {
                    return i;
                }
            }
        } else {
            for (int i3 = this.mFirstVisibleIndex; i3 <= this.mLastVisibleIndex; i3++) {
                int i4 = getLocation(i3).row;
                if (i4 == this.mNumRows - 1) {
                    z2 = true;
                } else if (z2 && i4 == 0) {
                    return i3;
                }
            }
        }
        return -1;
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Removed duplicated region for block: B:81:0x0105 A[LOOP:2: B:81:0x0105->B:95:0x0129, LOOP_START, PHI: r6 r9 r10 
      PHI: (r6v9 int) = (r6v3 int), (r6v12 int) binds: [B:80:0x0103, B:95:0x0129] A[DONT_GENERATE, DONT_INLINE]
      PHI: (r9v7 int) = (r9v5 int), (r9v8 int) binds: [B:80:0x0103, B:95:0x0129] A[DONT_GENERATE, DONT_INLINE]
      PHI: (r10v4 int) = (r10v2 int), (r10v6 int) binds: [B:80:0x0103, B:95:0x0129] A[DONT_GENERATE, DONT_INLINE]] */
    /* JADX WARNING: Removed duplicated region for block: B:98:0x0137  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean appendVisibleItemsWithoutCache(int r14, boolean r15) {
        /*
            r13 = this;
            androidx.leanback.widget.Grid$Provider r0 = r13.mProvider
            int r0 = r0.getCount()
            int r1 = r13.mLastVisibleIndex
            r2 = -2147483648(0xffffffff80000000, float:-0.0)
            r3 = 0
            r4 = 0
            r5 = 1
            if (r1 < 0) goto L_0x0075
            int r6 = r13.getLastIndex()
            if (r1 >= r6) goto L_0x0016
            return r4
        L_0x0016:
            int r1 = r13.mLastVisibleIndex
            int r6 = r1 + 1
            androidx.leanback.widget.StaggeredGrid$Location r1 = r13.getLocation(r1)
            int r1 = r1.row
            int r7 = r13.findRowEdgeLimitSearchIndex(r5)
            if (r7 >= 0) goto L_0x003f
            r8 = r2
            r7 = r4
        L_0x0028:
            int r9 = r13.mNumRows
            if (r7 >= r9) goto L_0x004d
            boolean r8 = r13.mReversedFlow
            if (r8 == 0) goto L_0x0035
            int r8 = r13.getRowMin(r7)
            goto L_0x0039
        L_0x0035:
            int r8 = r13.getRowMax(r7)
        L_0x0039:
            if (r8 == r2) goto L_0x003c
            goto L_0x004d
        L_0x003c:
            int r7 = r7 + 1
            goto L_0x0028
        L_0x003f:
            boolean r8 = r13.mReversedFlow
            if (r8 == 0) goto L_0x0048
            int r7 = r13.findRowMin(r4, r7, r3)
            goto L_0x004c
        L_0x0048:
            int r7 = r13.findRowMax(r5, r7, r3)
        L_0x004c:
            r8 = r7
        L_0x004d:
            boolean r7 = r13.mReversedFlow
            if (r7 == 0) goto L_0x0058
            int r7 = r13.getRowMin(r1)
            if (r7 > r8) goto L_0x0073
            goto L_0x005e
        L_0x0058:
            int r7 = r13.getRowMax(r1)
            if (r7 < r8) goto L_0x0073
        L_0x005e:
            int r1 = r1 + 1
            int r7 = r13.mNumRows
            if (r1 != r7) goto L_0x0073
            boolean r1 = r13.mReversedFlow
            if (r1 == 0) goto L_0x006d
            int r1 = r13.findRowMin(r4, r3)
            goto L_0x0071
        L_0x006d:
            int r1 = r13.findRowMax(r5, r3)
        L_0x0071:
            r8 = r1
            r1 = r4
        L_0x0073:
            r7 = r5
            goto L_0x0097
        L_0x0075:
            int r1 = r13.mStartIndex
            r6 = -1
            if (r1 == r6) goto L_0x007c
            r6 = r1
            goto L_0x007d
        L_0x007c:
            r6 = r4
        L_0x007d:
            androidx.collection.CircularArray<androidx.leanback.widget.StaggeredGrid$Location> r1 = r13.mLocations
            int r1 = r1.size()
            if (r1 <= 0) goto L_0x0091
            int r1 = r13.getLastIndex()
            androidx.leanback.widget.StaggeredGrid$Location r1 = r13.getLocation(r1)
            int r1 = r1.row
            int r1 = r1 + r5
            goto L_0x0092
        L_0x0091:
            r1 = r6
        L_0x0092:
            int r7 = r13.mNumRows
            int r1 = r1 % r7
            r7 = r4
            r8 = r7
        L_0x0097:
            r9 = r4
        L_0x0098:
            int r10 = r13.mNumRows
            if (r1 >= r10) goto L_0x014d
            if (r6 == r0) goto L_0x014c
            if (r15 != 0) goto L_0x00a8
            boolean r10 = r13.checkAppendOverLimit(r14)
            if (r10 == 0) goto L_0x00a8
            goto L_0x014c
        L_0x00a8:
            boolean r9 = r13.mReversedFlow
            if (r9 == 0) goto L_0x00b1
            int r9 = r13.getRowMin(r1)
            goto L_0x00b5
        L_0x00b1:
            int r9 = r13.getRowMax(r1)
        L_0x00b5:
            r10 = 2147483647(0x7fffffff, float:NaN)
            if (r9 == r10) goto L_0x00c9
            if (r9 != r2) goto L_0x00bd
            goto L_0x00c9
        L_0x00bd:
            boolean r10 = r13.mReversedFlow
            if (r10 == 0) goto L_0x00c5
            int r10 = r13.mSpacing
        L_0x00c3:
            int r10 = -r10
            goto L_0x00c7
        L_0x00c5:
            int r10 = r13.mSpacing
        L_0x00c7:
            int r9 = r9 + r10
            goto L_0x00fd
        L_0x00c9:
            if (r1 != 0) goto L_0x00ec
            boolean r9 = r13.mReversedFlow
            if (r9 == 0) goto L_0x00d7
            int r9 = r13.mNumRows
            int r9 = r9 - r5
            int r9 = r13.getRowMin(r9)
            goto L_0x00de
        L_0x00d7:
            int r9 = r13.mNumRows
            int r9 = r9 - r5
            int r9 = r13.getRowMax(r9)
        L_0x00de:
            if (r9 == r10) goto L_0x00fd
            if (r9 == r2) goto L_0x00fd
            boolean r10 = r13.mReversedFlow
            if (r10 == 0) goto L_0x00e9
            int r10 = r13.mSpacing
            goto L_0x00c3
        L_0x00e9:
            int r10 = r13.mSpacing
            goto L_0x00c7
        L_0x00ec:
            boolean r9 = r13.mReversedFlow
            if (r9 == 0) goto L_0x00f7
            int r9 = r1 + -1
            int r9 = r13.getRowMax(r9)
            goto L_0x00fd
        L_0x00f7:
            int r9 = r1 + -1
            int r9 = r13.getRowMin(r9)
        L_0x00fd:
            int r10 = r6 + 1
            int r6 = r13.appendVisibleItemToRow(r6, r1, r9)
            if (r7 == 0) goto L_0x0137
        L_0x0105:
            boolean r11 = r13.mReversedFlow
            if (r11 == 0) goto L_0x010e
            int r11 = r9 - r6
            if (r11 <= r8) goto L_0x0135
            goto L_0x0112
        L_0x010e:
            int r11 = r9 + r6
            if (r11 >= r8) goto L_0x0135
        L_0x0112:
            if (r10 == r0) goto L_0x0134
            if (r15 != 0) goto L_0x011d
            boolean r11 = r13.checkAppendOverLimit(r14)
            if (r11 == 0) goto L_0x011d
            goto L_0x0134
        L_0x011d:
            boolean r11 = r13.mReversedFlow
            if (r11 == 0) goto L_0x0126
            int r6 = -r6
            int r11 = r13.mSpacing
            int r6 = r6 - r11
            goto L_0x0129
        L_0x0126:
            int r11 = r13.mSpacing
            int r6 = r6 + r11
        L_0x0129:
            int r9 = r9 + r6
            int r6 = r10 + 1
            int r10 = r13.appendVisibleItemToRow(r10, r1, r9)
            r12 = r10
            r10 = r6
            r6 = r12
            goto L_0x0105
        L_0x0134:
            return r5
        L_0x0135:
            r6 = r10
            goto L_0x0147
        L_0x0137:
            boolean r6 = r13.mReversedFlow
            if (r6 == 0) goto L_0x0140
            int r6 = r13.getRowMin(r1)
            goto L_0x0144
        L_0x0140:
            int r6 = r13.getRowMax(r1)
        L_0x0144:
            r7 = r5
            r8 = r6
            goto L_0x0135
        L_0x0147:
            int r1 = r1 + 1
            r9 = r5
            goto L_0x0098
        L_0x014c:
            return r9
        L_0x014d:
            if (r15 == 0) goto L_0x0150
            return r9
        L_0x0150:
            boolean r1 = r13.mReversedFlow
            if (r1 == 0) goto L_0x0159
            int r1 = r13.findRowMin(r4, r3)
            goto L_0x015d
        L_0x0159:
            int r1 = r13.findRowMax(r5, r3)
        L_0x015d:
            r8 = r1
            r1 = r4
            goto L_0x0098
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.leanback.widget.StaggeredGridDefault.appendVisibleItemsWithoutCache(int, boolean):boolean");
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Removed duplicated region for block: B:80:0x00ff A[LOOP:2: B:80:0x00ff->B:94:0x0123, LOOP_START, PHI: r5 r8 r9 
      PHI: (r5v9 int) = (r5v3 int), (r5v12 int) binds: [B:79:0x00fd, B:94:0x0123] A[DONT_GENERATE, DONT_INLINE]
      PHI: (r8v7 int) = (r8v5 int), (r8v8 int) binds: [B:79:0x00fd, B:94:0x0123] A[DONT_GENERATE, DONT_INLINE]
      PHI: (r9v3 int) = (r9v1 int), (r9v5 int) binds: [B:79:0x00fd, B:94:0x0123] A[DONT_GENERATE, DONT_INLINE]] */
    /* JADX WARNING: Removed duplicated region for block: B:97:0x0131  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean prependVisibleItemsWithoutCache(int r13, boolean r14) {
        /*
            r12 = this;
            int r0 = r12.mFirstVisibleIndex
            r1 = 2147483647(0x7fffffff, float:NaN)
            r2 = 0
            r3 = 0
            r4 = 1
            if (r0 < 0) goto L_0x0072
            int r5 = r12.getFirstIndex()
            if (r0 <= r5) goto L_0x0011
            return r3
        L_0x0011:
            int r0 = r12.mFirstVisibleIndex
            int r5 = r0 + -1
            androidx.leanback.widget.StaggeredGrid$Location r0 = r12.getLocation(r0)
            int r0 = r0.row
            int r6 = r12.findRowEdgeLimitSearchIndex(r3)
            if (r6 >= 0) goto L_0x003c
            int r0 = r0 + -1
            int r6 = r12.mNumRows
            int r6 = r6 - r4
            r7 = r1
        L_0x0027:
            if (r6 < 0) goto L_0x004a
            boolean r7 = r12.mReversedFlow
            if (r7 == 0) goto L_0x0032
            int r7 = r12.getRowMax(r6)
            goto L_0x0036
        L_0x0032:
            int r7 = r12.getRowMin(r6)
        L_0x0036:
            if (r7 == r1) goto L_0x0039
            goto L_0x004a
        L_0x0039:
            int r6 = r6 + -1
            goto L_0x0027
        L_0x003c:
            boolean r7 = r12.mReversedFlow
            if (r7 == 0) goto L_0x0045
            int r6 = r12.findRowMax(r4, r6, r2)
            goto L_0x0049
        L_0x0045:
            int r6 = r12.findRowMin(r3, r6, r2)
        L_0x0049:
            r7 = r6
        L_0x004a:
            boolean r6 = r12.mReversedFlow
            if (r6 == 0) goto L_0x0055
            int r6 = r12.getRowMax(r0)
            if (r6 < r7) goto L_0x0070
            goto L_0x005b
        L_0x0055:
            int r6 = r12.getRowMin(r0)
            if (r6 > r7) goto L_0x0070
        L_0x005b:
            int r0 = r0 + -1
            if (r0 >= 0) goto L_0x0070
            int r0 = r12.mNumRows
            int r0 = r0 - r4
            boolean r6 = r12.mReversedFlow
            if (r6 == 0) goto L_0x006b
            int r6 = r12.findRowMax(r4, r2)
            goto L_0x006f
        L_0x006b:
            int r6 = r12.findRowMin(r3, r2)
        L_0x006f:
            r7 = r6
        L_0x0070:
            r6 = r4
            goto L_0x0097
        L_0x0072:
            int r0 = r12.mStartIndex
            r5 = -1
            if (r0 == r5) goto L_0x0079
            r5 = r0
            goto L_0x007a
        L_0x0079:
            r5 = r3
        L_0x007a:
            androidx.collection.CircularArray<androidx.leanback.widget.StaggeredGrid$Location> r0 = r12.mLocations
            int r0 = r0.size()
            if (r0 <= 0) goto L_0x0091
            int r0 = r12.getFirstIndex()
            androidx.leanback.widget.StaggeredGrid$Location r0 = r12.getLocation(r0)
            int r0 = r0.row
            int r6 = r12.mNumRows
            int r0 = r0 + r6
            int r0 = r0 - r4
            goto L_0x0092
        L_0x0091:
            r0 = r5
        L_0x0092:
            int r6 = r12.mNumRows
            int r0 = r0 % r6
            r6 = r3
            r7 = r6
        L_0x0097:
            r8 = r3
        L_0x0098:
            if (r0 < 0) goto L_0x0147
            if (r5 < 0) goto L_0x0146
            if (r14 != 0) goto L_0x00a6
            boolean r9 = r12.checkPrependOverLimit(r13)
            if (r9 == 0) goto L_0x00a6
            goto L_0x0146
        L_0x00a6:
            boolean r8 = r12.mReversedFlow
            if (r8 == 0) goto L_0x00af
            int r8 = r12.getRowMax(r0)
            goto L_0x00b3
        L_0x00af:
            int r8 = r12.getRowMin(r0)
        L_0x00b3:
            r9 = -2147483648(0xffffffff80000000, float:-0.0)
            if (r8 == r1) goto L_0x00c6
            if (r8 != r9) goto L_0x00ba
            goto L_0x00c6
        L_0x00ba:
            boolean r9 = r12.mReversedFlow
            if (r9 == 0) goto L_0x00c1
            int r9 = r12.mSpacing
            goto L_0x00c4
        L_0x00c1:
            int r9 = r12.mSpacing
        L_0x00c3:
            int r9 = -r9
        L_0x00c4:
            int r8 = r8 + r9
            goto L_0x00f7
        L_0x00c6:
            int r8 = r12.mNumRows
            int r8 = r8 - r4
            if (r0 != r8) goto L_0x00e6
            boolean r8 = r12.mReversedFlow
            if (r8 == 0) goto L_0x00d4
            int r8 = r12.getRowMax(r3)
            goto L_0x00d8
        L_0x00d4:
            int r8 = r12.getRowMin(r3)
        L_0x00d8:
            if (r8 == r1) goto L_0x00f7
            if (r8 == r9) goto L_0x00f7
            boolean r9 = r12.mReversedFlow
            if (r9 == 0) goto L_0x00e3
            int r9 = r12.mSpacing
            goto L_0x00c4
        L_0x00e3:
            int r9 = r12.mSpacing
            goto L_0x00c3
        L_0x00e6:
            boolean r8 = r12.mReversedFlow
            if (r8 == 0) goto L_0x00f1
            int r8 = r0 + 1
            int r8 = r12.getRowMin(r8)
            goto L_0x00f7
        L_0x00f1:
            int r8 = r0 + 1
            int r8 = r12.getRowMax(r8)
        L_0x00f7:
            int r9 = r5 + -1
            int r5 = r12.prependVisibleItemToRow(r5, r0, r8)
            if (r6 == 0) goto L_0x0131
        L_0x00ff:
            boolean r10 = r12.mReversedFlow
            if (r10 == 0) goto L_0x0108
            int r10 = r8 + r5
            if (r10 >= r7) goto L_0x012f
            goto L_0x010c
        L_0x0108:
            int r10 = r8 - r5
            if (r10 <= r7) goto L_0x012f
        L_0x010c:
            if (r9 < 0) goto L_0x012e
            if (r14 != 0) goto L_0x0117
            boolean r10 = r12.checkPrependOverLimit(r13)
            if (r10 == 0) goto L_0x0117
            goto L_0x012e
        L_0x0117:
            boolean r10 = r12.mReversedFlow
            if (r10 == 0) goto L_0x011f
            int r10 = r12.mSpacing
            int r5 = r5 + r10
            goto L_0x0123
        L_0x011f:
            int r5 = -r5
            int r10 = r12.mSpacing
            int r5 = r5 - r10
        L_0x0123:
            int r8 = r8 + r5
            int r5 = r9 + -1
            int r9 = r12.prependVisibleItemToRow(r9, r0, r8)
            r11 = r9
            r9 = r5
            r5 = r11
            goto L_0x00ff
        L_0x012e:
            return r4
        L_0x012f:
            r5 = r9
            goto L_0x0141
        L_0x0131:
            boolean r5 = r12.mReversedFlow
            if (r5 == 0) goto L_0x013a
            int r5 = r12.getRowMax(r0)
            goto L_0x013e
        L_0x013a:
            int r5 = r12.getRowMin(r0)
        L_0x013e:
            r6 = r4
            r7 = r5
            goto L_0x012f
        L_0x0141:
            int r0 = r0 + -1
            r8 = r4
            goto L_0x0098
        L_0x0146:
            return r8
        L_0x0147:
            if (r14 == 0) goto L_0x014a
            return r8
        L_0x014a:
            boolean r0 = r12.mReversedFlow
            if (r0 == 0) goto L_0x0153
            int r0 = r12.findRowMax(r4, r2)
            goto L_0x0157
        L_0x0153:
            int r0 = r12.findRowMin(r3, r2)
        L_0x0157:
            r7 = r0
            int r0 = r12.mNumRows
            int r0 = r0 - r4
            goto L_0x0098
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.leanback.widget.StaggeredGridDefault.prependVisibleItemsWithoutCache(int, boolean):boolean");
    }
}
