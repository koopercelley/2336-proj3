// Cooper Kelley (clk200002)

public class Node<T> {

  private Node<T> next, down, prev;
  private T payload;

  Node() {
    next = null;
    down = null;
    prev = null;
    payload = null;
  }

  Node(T pl) {
    payload = pl;
  }

  Node(Node<T> n, Node<T> d, Node<T> pr, T pl) {
    next = n;
    down = d;
    prev = pr;
    payload = pl;
  }

  void setNext(Node<T> n) {
    next = n;
  }

  void setDown(Node<T> d) {
    down = d;
  }

  void setPrev(Node<T> pr) {
    prev = pr;
  }

  void setPayload(T pl) {
    this.payload = pl;
  }

  Node<T> getNext() {
    return next;
  }

  Node<T> getDown() {
    return down;
  }

  Node<T> getPrev() {
    return prev;
  }

  T getPayload() {
    return payload;
  }
}
