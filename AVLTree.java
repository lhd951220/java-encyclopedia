/**
 * @author Haidong Liu
 * @date 2021/5/31 11:27
 */
public class AVLTree<E extends Comparable<E>> {
    private static class Node<E> {
        E e;
        Node<E> parent;
        Node<E> left;
        Node<E> right;
        int balance;

        public Node(E e, Node<E> parent) {
            this.e = e;
            this.parent = parent;
            this.balance = 0;
        }

        public E getE() {
            return e;
        }

        public void setE(E e) {
            this.e = e;
        }

        public Node<E> getParent() {
            return parent;
        }

        public void setParent(Node<E> parent) {
            this.parent = parent;
        }

        public Node<E> getLeft() {
            return left;
        }

        public void setLeft(Node<E> left) {
            this.left = left;
        }

        public Node<E> getRight() {
            return right;
        }

        public void setRight(Node<E> right) {
            this.right = right;
        }

        public int getBalance() {
            return balance;
        }

        public void setBalance(int balance) {
            this.balance = balance;
        }
    }

    private Node<E> root;

    private int size;

    private static final int BALANCE = 2;

    public AVLTree() {
        this.size = 0;
    }

    public boolean put(E e){
        Node<E> r = root;
        if(root == null){
            root = new Node<>(e, null);
            root.balance = 0;
            size++;
            return true;
        } else {
            Node<E> parent = r;
            int cmp;
            do {
                cmp = e.compareTo(r.e);
                parent = r;
                if(cmp > 0){
                    r = r.right;
                } else if(cmp < 0){
                    r = r.left;
                } else {
                    return false;
                }
            } while (r != null);

            Node<E> node = new Node<>(e, parent);
            if(cmp > 0){
                parent.right = node;
            } else {
                parent.left = node;
            }

            while(parent != null){

                int lh = height(parent.left);
                int rh = height(parent.right);

                int heightDiff = lh - rh;
                if(Math.abs(heightDiff) == 2){
                    fixAfterInsertion(parent, heightDiff);
                } else {
                    parent = parent.parent;
                }
            }
        }
        size++;
        return true;
    }

    public boolean remove(E e){
        if(e == null) return false;
        Node<E> r = root;

        do{
            int cmp = e.compareTo(r.e);
            if(cmp < 0){
                r = r.left;
            } else if(cmp > 0){
                r = r. right;
            } else {
                break
            }
        }while(r != null);

        Node<E> p = r.parent;

        if(r.left == null && r.right == null){
            if(p == null){
                this.root = null;
            }else {
                if(r.parent.left == r){
                    r.parent.left = null;
                } else {
                    r.parent.right = null;
                }
                r.parent = null;
            }
        } else if(r.left != null && r.right == null){
            if(p == null){
                this.root = r.left;
                this.root.parent = null;
            } else {
                if(r.parent.left == r){
                    r.parent.left = r.left;
                } else {
                    r.parent.right = r.left;
                }
                r.left.parent = p;
            }
            r.parent = null;
        } else if(r.left == null && r.right != null){
            if(p == null){
                this.root = r.right;
                this.root.parent = null; 
            } else {
                if(r.parent.left == r){
                    r.parent.left = r.right;
                } else {
                    r.parent.right = r.right;
                }
                r.right.parent = p;
            }
            r.parent = null;
        } else {
            // 如果待删除节点拥有左右节点时，则查看左右节点子树的高度，如果左子树较高，则使用前驱节点进行替换，如果右子树较高，则使用后继节点进行替换
            // 最后再查看树是否平衡。
            lHeight = height(r.left);
            rHeight = height(r.right);
            Node<E> n;
            if(lHeight > rHeight){
                n = predecessor(r);
                if(n.left != null){
                    n.parent.right = n.left;
                }
            } else {
                n = successor(r);
                if(n.right != null){
                    n.parent.left = n.right;
                }
            }
            r.e = n.e;
        }
    }

    private void fixAfterInsertion(Node<E> parent, int heightDiff) {
        if(heightDiff == BALANCE){
            Node<E> l = parent.left;
            int i = height(l.left) - height(l.right);
            if(i < 0){
                rotateLeft(l);
            }
            rotateRight(parent);
        } else {
            Node<E> r = parent.right;
            int i = height(r.left) - height(r.right);
            if(i > 0){
                rotateRight(parent.right);
            }
            rotateLeft(parent);
        }
    }

    /**
     * 中序遍历中，指定节点的后一个节点即是后继者
     * 1. 如果节点存在右子树，则取右子树中最左边的值，即是后继者
     * 2. 如果节点不存在右子树，则取父节点，如果不是父节点的右节点不是本节点，那么父节点就是后继者
     *
     * @param e
     * @return
     */
    private Node<E> successor(Node<E> e){
        if(e == null){
            return null;
        } else if(e.right != null){
            Node<E> l = e.right;
            while(l.left != null){
                l = l.left;
            }
            return l;
        } else {
            Node<E> p = e.parent;
            Node<E> ch = e;
            while(p != null && p.right == ch){
                ch = p;
                p = p.parent;
            }
            return p;
        }
    }

    private Node<E> predecessor(Node<E> e){
        if(e == null){
            return null;
        } else if(e.left != null){
            Node<E> r = e.left;
            while(r.right != null){
                r = r.right;
            }
            return r;
        } else {
            Node<E> p = e.parent;
            Node<E> ch = e;
            while(p != null && p.left == ch){
                ch = p;
                p = p.parent;
            }
        }
    }

    private int height(Node<E> e){
        if(e == null){
            return 0;
        }

        return Math.max(height(e.left), height(e.right)) + 1;
    }

    private void rotateRight(Node<E> e){
        if(e != null){
            Node<E> l = e.left;
            e.left = l.right;
            if(l.right != null) {
                l.right.parent = e;
            }
            l.parent = e.parent;
            if(e.parent == null) {
                root = l;
            } else if(e.parent.right == e){
                e.parent.right = l;
            } else {
                e.parent.left = l;
            }
            l.right = e;
            e.parent = l;
        }
    }

    private void rotateLeft(Node<E> e){
        if(e != null){
            Node<E> r = e.right;
            e.right = r.left;
            if(r.left != null) {
                r.left.parent = e;
            }
            r.parent = e.parent;
            if(e.parent == null){
                root = r;
            } else if(e.parent.right == e){
                e.parent.right = r;
            } else {
                e.parent.left = r;
            }
            r.left = e;
            e.parent = r;
        }
    }
}
