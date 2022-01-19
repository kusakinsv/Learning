package ru.one.tests.concurrentCounter;

// класс выполняющий какие-либо действия в многопоточном режиме


import java.util.List;

public class ConcurrentCount<T> {
    private List<T> OBJECT;
    private boolean i;
    private int counterPerThread = 200; //количество выполнений на поток
    private int threadAmount;
    private int amount; //количество
    private int MAX_OPERATIONS = 10000;




void start() {
//    int counter = amount;
//    if (counter > 2000) counter = 2000;
//    if(MAX_OPERATIONS < counter) counter = MAX_OPERATIONS;
//    int leftover = 0; //jcnfnjr
//    int count = counterPerThread;
//    System.out.printf("counter: %s\n", counter);
//    if (counter <= perPage) {
//        countpages = 1;
//        count = counter;}
//    if (counter > perPage) {
//        if ((counter%perPage) !=0) {
//            leftover = counter % perPage;
//            countpages = (counter - leftover) / perPage;
//            if (leftover > 0) countpages = countpages+1;
//        } else {
//            countpages = counter/perPage;}
//    }
//
//
//
//}
//
//
//    public ConcurrentCount(int amount) {
//        this.amount = amount;

}
}