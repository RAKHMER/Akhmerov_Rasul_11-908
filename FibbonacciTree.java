public class FibbonacciTree {
    int size;
    Node min;

    //ВСТАВКА ЭЛЕМЕНТА
    public void insert(int x) {
        Node newNode = new Node();  //создание нового узла
        newNode.key = x;    //инициализация ключа нового узла
        if (size == 0) {    //в случае если в куче не было элементов, вставляется только добавленный
            min = newNode;
            min.left = newNode;
            min.right = newNode;
        }else {             //иначе, меняем указатели списка
            Node prevRight = min.right;
            min.right = newNode;
            newNode.left = min;
            newNode.right = prevRight;
            prevRight.left = newNode;
        }
        if (newNode.key<min.key)    //если вставленный элемент меньше минимума, мы меняем указатель минимума кучи
            min = newNode;
        newNode.parent = null;
        size++;                     //увеличение переменной "размер"
    }

    //СОЕДИНЕНИЕ ДВУХ КУЧ
    //объеденение списков вершин, данные в виде элементов
    public void unionLists( Node first,Node second) {    //меняем указатели списка
        Node L = first.left;
        Node R = second.right;
        second.right = first;
        first.left = second;
        L.right = R;
        R.left = L;
    }
    //слияние двух корневых списков в один и..
    public void merge(fibonacciHeap that) {
        if (that.size == 0)         //в случае, если вторая куча пустая, то без изменений
            return;
        if (size == 0){             //в случае, если первая куча пустая, то результатом будет являться вторая
            min = that.min;
            size = that.size;
        } else {                        //в ином случае, объеденение двух корневых списка
            unionLists(min, that.min);
            size += that.size;
        }
        //..обновление указателя минимума в случае, если минимум изменяется
        if (min == null || (that.min!=null && that.min.key<min.key))
            min = that.min;
    }

    //УДАЛЕНИЕ МИНИМАЛЬНОГО ЭЛЕМЕНТА
    public int deleteMin(){
        Node prevMin = min;
        unionLists(min, min.child);
        Node L = min.left;
        Node R = min.right;
        L.right = R;
        R.left = L;
        if (prevMin.right == prevMin) {
            min = null;
            return 0;
        }
        min = min.right;
        consolidate();
        size--;
        return prevMin.key;
    }

    //ПРОРЕЖЕНИЕ ДЕРЕВЬЕВ
    public void consolidate() {
        Node[] A = new Node[min.degree+16];
        A[min.degree] = min;                    //создание массива и инициализация его минимума
        Node current = min.right;
        while (A[current.degree] != current){   //пока происходит изменение массивов
            if (A[current.degree] == null) {    //в случае, если ячейка пуста, впомещаем ее в нее текущий элемент
                A[current.degree] = current;
                current = current.right;
            } else {                            //в ином случае, подвесим к меньшему текущий элемент
                Node conflict = A[current.degree];
                Node addTo, adding;
                if (conflict.key < current.key) {
                    addTo = conflict;
                    adding = current;
                } else {
                    addTo = current;
                    adding = conflict;
                }
                unionLists(addTo.child, adding);
                adding.parent = addTo;
                addTo.degree++;
                current = addTo;
            }                                   //при необходимости обновляется минимум
            if (min.key > current.key)
                min = current;
        }
    }

    //ВЫРЕЗАНИЕ ВЕРШИНЫ
    public void cut(Node x) {
        Node L = x.left;
        Node R = x.right;   //удаление текущей вершины
        R.left = L;
        L.right = R;
        x.parent.degree--;
        if (x.parent.child == x)    //проверка для сохранения родительской ссылки на детей
            if (x.right == x)       //в случае если вершина, вырезаемая нами содержится в родителе, изменяем ее на соседнюю
                x.right = x.parent;
            else                    //в ином случае у родителей нет детей
                x.parent.child = x.right;
        x.right = x;
        x.left = x;
        x.parent = null;
        unionLists(min, x);         //вставка поддерева в корневой список
    }

    //УМЕНЬШЕНИЕ ЗНАЧЕНИЯ ЭЛЕМЕНТА
    public void decreaseKey(Node x, int newValue) {
        if (newValue > x.parent.key) {   //в случае, если новое значение ключа не меньше родительского значения ключа, присваиваем новое знчания и выходим
            x.key = newValue;
            return;
        }
        Node parent = x.parent;          //иначе, вызываем следующие операции, а именно:
        cut(x);                          //вырезание
        cascadingCut(parent);            //каскадное вырезание
    }

    //КАСКАДНОЕ ВЫРЕЗАНИЕ ВЕРШИНЫ
    //вершина помечаенная, в случае, если у нее не удаляли дочерний узел, что нам известно перед вызывом каскатного выражения
    public void cascadingCut(Node x) {
        while (x.mark = true){  //пока не меченная
            cut(x);             //вырезание текущей вершины
            x = x.parent;       //запуск каскадного вырезания от родителя
        }
        x.mark = true;          //пометить, так как этой вершины удалили ребенка
    }

    //УДАЛЕНИЕ ЭЛЕМЕНТА
    public void delete(Node x) {
        decreaseKey(x,Integer.MIN_VALUE);
        deleteMin();
    }
}
