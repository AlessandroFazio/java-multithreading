package org.example.module7.example1;

import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class CryptoAssetsApplication {
    public static void main(String[] args) {

    }

    public static class PricesContainer {
        private Lock lock = new ReentrantLock();
        private volatile double bitcoinPrice;
        private volatile double etherPrice;
        private volatile double litecoinPrice;
        private volatile double bitcoinCashPrice;
        private volatile double ripplePrice;

        public Lock getLock() {
            return lock;
        }

        public void setLock(Lock lock) {
            this.lock = lock;
        }

        public double getBitcoinPrice() {
            return bitcoinPrice;
        }

        public void setBitcoinPrice(double bitcoinPrice) {
            this.bitcoinPrice = bitcoinPrice;
        }

        public double getEtherPrice() {
            return etherPrice;
        }

        public void setEtherPrice(double etherPrice) {
            this.etherPrice = etherPrice;
        }

        public double getLitecoinPrice() {
            return litecoinPrice;
        }

        public void setLitecoinPrice(double litecoinPrice) {
            this.litecoinPrice = litecoinPrice;
        }

        public double getBitcoinCashPrice() {
            return bitcoinCashPrice;
        }

        public void setBitcoinCashPrice(double bitcoinCashPrice) {
            this.bitcoinCashPrice = bitcoinCashPrice;
        }

        public double getRipplePrice() {
            return ripplePrice;
        }

        public void setRipplePrice(double ripplePrice) {
            this.ripplePrice = ripplePrice;
        }
    }

    public static class PriceUpdater extends Thread {
        private PricesContainer pricesContainer;
        private final Random RANDOM = new Random();

        public PriceUpdater(PricesContainer pricesContainer) {
            this.pricesContainer = pricesContainer;
        }

        @Override
        public void run() {
            while(true) {
                pricesContainer.getLock().lock();

                try {
                    try {
                    Thread.sleep(1000);
                    } catch (InterruptedException ignored) {
                }
                    pricesContainer.setBitcoinPrice(RANDOM.nextDouble() * 10000);
                    pricesContainer.setEtherPrice(RANDOM.nextDouble() * 2000);
                    pricesContainer.setLitecoinPrice(RANDOM.nextDouble() * 100);
                    pricesContainer.setBitcoinCashPrice(RANDOM.nextDouble() * 1200);
                    pricesContainer.setRipplePrice(RANDOM.nextDouble() * 1);
                }
                finally {
                    pricesContainer.getLock().unlock();
                }

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ignored) {
                }
            }
        }
    }
}
