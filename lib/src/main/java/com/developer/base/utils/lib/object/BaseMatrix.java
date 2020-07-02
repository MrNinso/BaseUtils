package com.developer.base.utils.lib.object;

import java.util.Collection;

public class BaseMatrix<T> {

    private final Node<T> mMasterNode;

    public BaseMatrix() {
        mMasterNode = new Node<>(0,0);
    }

    public void put(int i, int j, T t) {
        if (i == 0 && j == 0) {
            mMasterNode.Data = t;
            return;
        }

        AbstractNode parentNode = moveFrom(mMasterNode, i, false);
        AbstractNode tmpNode;

        if (i != 0)
            if (parentNode.RightNode == null) {
                tmpNode = new NullNode(i,0);
                parentNode.RightNode = tmpNode;
            } else {
                tmpNode = parentNode.RightNode;
            }
        else
            tmpNode = parentNode;

        Node<T> newNode = new Node<>(i, j, t);

        if (tmpNode.I == i) {
            if (j == 0) {
                if (tmpNode instanceof Node) {
                    ((Node<T>) tmpNode).Data = t;
                } else {
                    newNode.BelowNode = tmpNode.BelowNode;
                    newNode.RightNode = tmpNode.RightNode;
                    parentNode.RightNode = newNode;
                }
            } else {
                parentNode = moveFrom(tmpNode, j, true);
                if (parentNode.BelowNode != null) {
                    newNode.BelowNode = parentNode.BelowNode.BelowNode;
                }
                parentNode.BelowNode = newNode;
            }
        } else {
            if (j == 0) {
                newNode.RightNode = tmpNode;
                parentNode.RightNode = newNode;
            } else {
                NullNode nullNode = new NullNode(i, 0);
                nullNode.RightNode = tmpNode;

                parentNode.RightNode = nullNode;
                nullNode.BelowNode = newNode;
            }
        }

    }

    public void put(Node<T> node) {
        put(node.I, node.J, node.Data);
    }

    public void putAll(Collection<Node<T>> nodes) {
        BaseList.forEach(nodes, (index, tNode) ->
                put(tNode)
        );
    }

    public void putAllAbsent(Collection<Node<T>> nodes) {
        BaseList.forEach(nodes, (index, tNode) ->
                putIfAbsent(tNode)
        );
    }

    public boolean putIfAbsent(int i, int j, T t) {
        T old = get(i, j);
        if (old == null) {
            put(i, j, t);
            return true;
        }

        return false;
    }

    public boolean putIfAbsent(Node<T> node) {
        return putIfAbsent(node.I, node.J, node.Data);
    }

    public T get(int i, int j) {
        AbstractNode t = getNode(i, j);

        if (t == null)
            return null;
        else
            return t instanceof Node ? ((Node<T>) t).Data : null;
    }

    public boolean remove(int i, int j) {
        if (i == 0 && j == 0) {
            boolean r = mMasterNode.Data != null;
            mMasterNode.Data = null;

            return r;
        } else if (j != 0 && i == 0) {
            AbstractNode tmpNode = moveFrom(mMasterNode, j, true);
            if (tmpNode == null || tmpNode.BelowNode == null || tmpNode.BelowNode.J != j)
                return false;

            if (tmpNode.BelowNode.BelowNode == null) {
                tmpNode.BelowNode = null;
                return true;
            } else {
                NullNode nullNode = new NullNode(i,j);
                nullNode.BelowNode = tmpNode.BelowNode.BelowNode;
                tmpNode.BelowNode = nullNode;
            }
        } else if (j == 0) {
            AbstractNode tmpNode = moveFrom(mMasterNode, i, false);
            if (tmpNode == null || tmpNode.RightNode == null || tmpNode.RightNode.I != i)
                return false;

            if (tmpNode.RightNode.RightNode == null && tmpNode.RightNode.BelowNode == null) {
                tmpNode.RightNode = null;
            } else {
                NullNode nullNode = new NullNode(i,j);
                nullNode.BelowNode = tmpNode.RightNode.BelowNode;
                nullNode.RightNode = tmpNode.RightNode.RightNode;
                tmpNode.RightNode = nullNode;
            }
        } else {
            AbstractNode tmpNode = moveFrom(mMasterNode, i, false);
            if (tmpNode == null || tmpNode.RightNode == null || tmpNode.RightNode.I != i)
                return false;

            tmpNode = moveFrom(tmpNode.RightNode, j, true);

            if (tmpNode == null || tmpNode.BelowNode == null || tmpNode.BelowNode.J != j)
                return false;

            if (tmpNode.BelowNode.BelowNode == null) {
                tmpNode.BelowNode = null;
            } else {
                NullNode nullNode = new NullNode(i,j);
                nullNode.BelowNode = tmpNode.BelowNode.BelowNode;
                tmpNode.BelowNode = nullNode;
            }
        }
        return true;
    }

    public T removeIf(RemoveIf<T> r) {
        final Node<T>[] oldNode = new Node[1];

        forEachItemBreakable((i, j, t1) -> {
            if (r.remove(i,j, t1, 0)) {
                oldNode[0] = new Node<>(i,j, t1);
                return EachBreakable.BREAK;
            }
            return EachBreakable.CONTINUE;
        });

        if (oldNode[0] != null) {
            remove(oldNode[0].I, oldNode[0].J);
            return oldNode[0].Data;
        }

        return null;
    }

    public int getItemCount() {
        final int[] count = { 0 };

        forEachItem((i, j, t) -> count[0]++);

        return count[0];
    }

    public int[] getMatrixSize() {
        int maxI = 0, maxJ = 0;

        for (AbstractNode c = mMasterNode; c != null; c = c.RightNode ) {
            for (AbstractNode n = c; n != null; n = n.BelowNode) {
                if (n.I > maxI) {
                    maxI = n.I;
                }

                if (n.J > maxJ) {
                    maxJ = n.J;
                }
            }
        }

        return new int[] { maxI, maxJ };
    }

    public void forEach(boolean justNotNull, Each<T> f) {
        if (justNotNull) {
            forEachItem(f);
        } else {
            forEachPosition(f);
        }
    }

    public void forEachBreakable(boolean justNotNull, EachBreakable<T> f) {
        if (justNotNull) {
            forEachItemBreakable(f);
        } else {
            forEachPositionBreakable(f);
        }
    }

    public void forEachPosition(Each<T> f) {
        int[] matrixSize = getMatrixSize();

        for (int i = 0; i < matrixSize[0]; i++) {
            for (int j = 0; j < matrixSize[1]; j++) {
                f.each(i,j, get(i, j));
            }
        }
    }

    public void forEachPositionBreakable(EachBreakable<T> f) {
        byte r = EachBreakable.CONTINUE;
        int[] matrixSize = getMatrixSize();

        for (int i = 0; i < matrixSize[0]; i++) {
            if (r == EachBreakable.SKIP_NEXT) {
                r = EachBreakable.CONTINUE;
                continue;
            } else if (r == EachBreakable.BREAK) {
                break;
            }

            for (int j = 0; j < matrixSize[1]; j++) {
                if (r == EachBreakable.SKIP_NEXT) {
                    r = EachBreakable.CONTINUE;
                    continue;
                } else if (r == EachBreakable.BREAK) {
                    break;
                }
                r = f.each(i, j, get(i, j));
            }
        }
    }

    public void forEachItem(Each<T> f) {
        for (AbstractNode c = mMasterNode; c != null; c = c.RightNode ) {
            for (AbstractNode n = c; n != null; n = n.BelowNode) {
                if (n instanceof Node)
                    f.each(n.I, n.J, ((Node<T>) n).Data);
            }
        }
    }

    public void forEachItemBreakable(EachBreakable<T> f) {
        byte r = EachBreakable.CONTINUE;
        for (AbstractNode c = mMasterNode; c != null; c = c.RightNode ) {
            if (r == EachBreakable.SKIP_NEXT) {
                r = EachBreakable.CONTINUE;
                continue;
            } else if (r == EachBreakable.BREAK) {
                break;
            }

            for (AbstractNode n = c; n != null; n = n.BelowNode) {
                if (r == EachBreakable.SKIP_NEXT) {
                    r = EachBreakable.CONTINUE;
                    continue;
                } else if (r == EachBreakable.BREAK) {
                    break;
                }

                if (n instanceof Node)
                    r = f.each(n.I, n.J, ((Node<T>) n).Data);
            }
        }
    }

    public int countIf(boolean justNotNull, Count<T> c) {
        final int[] count = {0};

        forEach(justNotNull, (i, j, t) -> count[0] += (c.count(i, j, t, count[0])) ? 1 : 0);

        return count[0];
    }

    public int[] search(boolean justNotNull, SearchMatrix<T> s) {
        final int[] position = new int[] {-1, -1};

        forEachBreakable(justNotNull, (i, j, t) -> {
            if (s.isItem(i, j, t)) {
                position[0] = i;
                position[1] = j;
                return EachBreakable.BREAK;
            }
            return EachBreakable.CONTINUE;
        });

        return position;
    }

    public <O> BaseList<O> extractList(boolean justNotNull, ExtractList<T, O> ex) {
        BaseList<O> r = new BaseList<>();

        forEach(justNotNull, (i, j, t) -> {
            O o = ex.extract(i, j, t, r.size());
            if (o != null) {
                r.add(o);
            }
        });

        return r;
    }

    public <OK, OV> BaseMap<OK, OV> extractMap(boolean justNotNull, ExtractMap<T, OK, OV> e) {
        BaseMap<OK, OV> r = new BaseMap<>();

        forEach(justNotNull, (i, j, t) -> {
            BaseEntry<OK, OV> b = e.extract(i,j, t, r.size());

            if (b != null) {
                r.put(b);
            }
        });

        return r;
    }

    public <O> BaseMatrix<O> extractMatrix(boolean justNotNull, ExtractMatrix<T, O> e) {
        final int[] size = new int[] { 0, 0 };
        final int[] count = new int[] { 0 };
        BaseMatrix<O> matrix = new BaseMatrix<>();


        forEach(justNotNull, (i, j, t) -> {
            Node<O> n = e.extract(i, j, t, size[0], size[1], count[0]);
            if (n != null) {
                if (n.I > size[0])
                    size[0] = n.I;

                if (n.J > size[1])
                    size[1] = n.J;

                matrix.put(n);

                count[0]++;
            }
        });

        return matrix;
    }

    private AbstractNode getNode(int i, int j) {
        if (i == 0 && j == 0)
            return mMasterNode;

        AbstractNode parentNode = moveFrom(mMasterNode, i, false);

        if (parentNode.RightNode == null || parentNode.RightNode.I != i) {
            return null;
        }

        if (parentNode.RightNode.J == j) {
            return parentNode.RightNode;
        }

        parentNode = moveFrom(parentNode.RightNode, j, true);

        if (parentNode.BelowNode == null || parentNode.BelowNode.J != j) {
            return null;
        }

        return parentNode.BelowNode;
    }

    private AbstractNode moveFrom(AbstractNode rootNode, int d, boolean belowNode) {
        AbstractNode parentNode = rootNode;
        AbstractNode tmpNode = rootNode;

        for (int k = 0; k <= d; k++) {
            if (tmpNode == null) {
                break;
            }

            if (belowNode) {
                if (tmpNode.J == d || tmpNode.J > d) {
                    break;
                }
            } else {
                if (tmpNode.I == d || tmpNode.I > d) {
                    break;
                }
            }

            parentNode = tmpNode;
            tmpNode = belowNode ? tmpNode.BelowNode : tmpNode.RightNode;
        }

        return parentNode;
    }

    public interface Each<T> {
        void each(int i, int j, T t);
    }

    public interface EachBreakable<T> {
        byte BREAK = 0x0;
        byte CONTINUE = 0x1;
        byte SKIP_NEXT = 0x2;

        byte each(int i, int j, T t);
    }

    public interface RemoveIf<T> {
        boolean remove(int i, int j, T t, int countRemoveList);
    }

    public interface ExtractList<T, O> {
        O extract(int i, int j, T t, int count);
    }

    public interface ExtractMap<T, OK, OV> {
        BaseEntry<OK, OV> extract(int i, int j, T t, int count);
    }

    public interface ExtractMatrix<T, O> {
        Node<O> extract(int i, int j, T t, int sizeI, int sizeJ, int count);
    }

    public interface Count<T> {
        boolean count(int i, int j, T t, int count);
    }

    public interface SearchMatrix<T> {
        boolean isItem(int i, int j, T t);
    }

    public static class Node<T> extends AbstractNode implements Cloneable {
        T Data;

        public Node(int i, int j) {
            super(i, j);
        }

        public Node(int i, int j, T data) {
            super(i, j);
            Data = data;
        }
    }

    private static abstract class AbstractNode {
        final int I, J;
        protected AbstractNode RightNode, BelowNode;

        public AbstractNode(int i, int j) {
            this.I = i;
            this.J = j;
        }

    }

    private static class NullNode extends AbstractNode {
        public NullNode(int i, int j) {
            super(i, j);
        }
    }

}