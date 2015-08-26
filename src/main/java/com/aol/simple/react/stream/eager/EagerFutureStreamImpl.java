package com.aol.simple.react.stream.eager;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.stream.Collector;
import java.util.stream.Stream;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.Wither;
import lombok.extern.slf4j.Slf4j;

import com.aol.simple.react.async.factories.QueueFactories;
import com.aol.simple.react.async.factories.QueueFactory;
import com.aol.simple.react.async.subscription.AlwaysContinue;
import com.aol.simple.react.async.subscription.Continueable;
import com.aol.simple.react.capacity.monitor.LimitingMonitor;
import com.aol.simple.react.collectors.lazy.BatchingCollector;
import com.aol.simple.react.collectors.lazy.LazyResultConsumer;
import com.aol.simple.react.stream.BaseSimpleReact;
import com.aol.simple.react.stream.EagerStreamWrapper;
import com.aol.simple.react.stream.traits.EagerFutureStream;
import com.nurkiewicz.asyncretry.RetryExecutor;


@Wither
@Builder
@Getter
@Slf4j
@AllArgsConstructor(access=AccessLevel.PRIVATE)
public class EagerFutureStreamImpl<U> implements EagerFutureStream<U>{
	


	private final Optional<Consumer<Throwable>> errorHandler;
	private final EagerStreamWrapper lastActive;
	private final QueueFactory<U> queueFactory;
	private final EagerReact simpleReact;
	private final Continueable subscription;
	

	/**
	 * 
	 * Construct a SimpleReact stage - this acts as a fluent SimpleReact builder
	 * 
	 @param eagerReact
	 *            Management for each stage
	 *            
	 * @param stream
	 *            Stream that will generate the events that will be reacted to.
	 * 
	 */
	public EagerFutureStreamImpl(EagerReact eagerReact, final Stream<CompletableFuture<U>> stream) {
		this.simpleReact =eagerReact;

		Stream s = stream;
		this.lastActive = new EagerStreamWrapper(s, true);
		this.errorHandler = Optional.of((e) -> log.error(e.getMessage(), e));
		

	
		this.queueFactory = QueueFactories.unboundedQueue();
		subscription = new AlwaysContinue();
		
	}
	
	
	@Override
	public <R, A> R collect(Collector<? super U, A, R> collector) {
		return block(collector);
	}

	public BaseSimpleReact getPopulator(){
		return null;
	}
	public void returnPopulator(BaseSimpleReact service){
		
	}
	
	@Override
	public EagerFutureStream<U> withAsync(boolean b) {
		
		return this.withSimpleReact(this.simpleReact.withAsync(b));
	}



	@Override
	public Executor getTaskExecutor() {
		return this.simpleReact.getExecutor();
	}



	@Override
	public RetryExecutor getRetrier() {
		return this.simpleReact.getRetrier();
	}



	@Override
	public boolean isAsync() {
		return this.simpleReact.isAsync();
	}



	@Override
	public EagerFutureStream<U> withTaskExecutor(Executor e) {
		return this.withSimpleReact(simpleReact.withExecutor(e));
	}



	@Override
	public EagerFutureStream<U> withRetrier(RetryExecutor retry) {
		return this.withSimpleReact(simpleReact.withRetrier(retry));
	}
	
}