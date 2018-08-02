public class TestStaticInnerClazz {
    static private void say(String message){ System.out.println(message); }
    private void non_static_say(String message) { System.out.println(message); }

    public static void main(String [] args) {
        /** 可以在静态方法中直接访问静态内部类的静态方法 */
        InnerStaticClazz.inner_say("Calling inner say()");

        /** 也可以实例化内部静态类，访问成员方法　*/
        InnerStaticClazz innerStatic = new InnerStaticClazz();
        innerStatic.inner_non_static_say("Call inner non-static say()");

        /** 但是不能在静态方法中实例化内部非静态类，因为静态方法不能提供内部非静态类实例所需的外部实例的引用 */
        //InnerNonStaticClazz innerNonStatic = new InnerNonStaticClazz();

        /** 除非被实例化的就是外部类自己 */
        TestStaticInnerClazz testClazz = new TestStaticInnerClazz();
        testClazz.testInnerClazzes();
    }

    private void testInnerClazzes() {
        /** 可以在非静态方法中访问内部静态类的静态方法　*/
        InnerStaticClazz.inner_say("Calling inner say() from inner method");

        /** 也可以实例化静态类，然后访问成员方法 */
        InnerStaticClazz innerStatic = new InnerStaticClazz();
        innerStatic.inner_non_static_say("Call inner non-static say() from inner method");
        InnerStaticClazz.inner_static_say_with_reference(this, "Call inner non-static say() from inner method with owner");

        /** 对内部非静态类也没有限制 */
        InnerNonStaticClazz innerNonStatic = new InnerNonStaticClazz();
        innerNonStatic.call_outter_non_static_say("Call inner non-static non-static say() from inner method");
        innerNonStatic.call_outter_static_say("Call static say() from inner non-static class");
    }

    static class InnerStaticClazz {
        /**
         * 静态类是静态的，它与具体实例无关．因此只能访问其他类（包括外部类）的静态方法，除非持有外部实例的引用．
         */
        static void inner_say(String message) {
            /**
             * 内部静态类可以访问外部类的静态方法
             */
            say(message);
        }

        void inner_non_static_say(String message) {
            /**
             * 不可以访问非静态方法，因为不持有外部引用
             */
            //non_static_say(message);
        }

        static void inner_static_say_with_reference(TestStaticInnerClazz owner, String message) {
            /**
             * 除非持有引用
             */
            owner.non_static_say(message);
        }
    }

    class InnerNonStaticClazz {
        /**
         * 内部非静态类天然持有对外部实例的引用
         */
        void call_outter_non_static_say(String message) {
            /**
             * 因此可以直接访问外部非静态方法
             */
            non_static_say(message);
        }

        void call_outter_static_say(String message) {
            /**
             * 也可以访问外部静态方法
             */
            say(message);
        }

        /** static*/ void inner_say(String message){
            /**
             * 限制是：非静态内部类不能拥有静态方法，因为非静态内部类是与（外部）类实例相关的，但是静态方法应该与任何实例无关．
             */
            System.out.print(message);
        }
    }
}
