package com.javayh.advanced.java.datastructure.stack;

/**
 * <p>
 * 测试
 * </p>
 *
 * @author hai ji
 * @version 1.0.0
 * @since 2020-08-19
 */
public class StackTest {
    public static void main(String[] args) {
        Stack<String> stack = new StackArray<>();
        stack.push("haiji");
        stack.push("yang");
        String pop = stack.pop();
        System.out.println(pop);
        Stack<String> stackList = new LinkedStack<>();
        stackList.push("haiji");
        stackList.push("yang");
        String popList = stackList.pop();
        System.out.println(popList);
    }
}
