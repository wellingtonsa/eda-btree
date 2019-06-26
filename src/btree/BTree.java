package btree;

import java.util.LinkedList;
import java.util.Queue;

public class BTree<K extends Comparable<K>, V> {

	private Page<K> root;
	private PageSerializer<K> pageSerializer;
	private DataSerializer<V> dataSerializer;

	public BTree(PageSerializer<K> pageSerializer, DataSerializer<V> dataSerializer) throws Exception {
		this.pageSerializer = pageSerializer;
		this.dataSerializer = dataSerializer;
		this.root = pageSerializer.readRoot();
	}

	public V get(K key) throws Exception {
		return get(root, key);
	}

	private V get(Page<K> r, K key) throws Exception {
		if (r.isExternal()) {
			Long offset = r.getDataOffset(key);
			return dataSerializer.read(offset);
		}
		return get(r.next(key), key);
	}

	public boolean contains(K key) throws Exception {
		return contains(root, key);
	}

	private boolean contains(Page<K> r, K key) throws Exception {
		if (r.isExternal())
			return r.holds(key);
		return contains(r.next(key), key);
	}

	public void put(K key, V value) throws Exception {
		put(root, key, value);
		if (root.isOverflowed()) {
			Page<K> newRoot = new Page<>(false, PageSerializer.ROOT_OFFSET, pageSerializer);
			Page<K> right = root.split();
			Page<K> left = pageSerializer.append(root.asSymbolTable(), root.isExternal());

			newRoot.enter(left);
			newRoot.enter(right);
			newRoot.close();

			root = newRoot;
		}
	}

	private void put(Page<K> r, K key, V value) throws Exception {

		if (r.isExternal()) {
			if (!r.holds(key)) {
				long offset = dataSerializer.append(value);
				r.insert(key, offset);
				pageSerializer.keyInserted();
				r.close();
			} else
				dataSerializer.write(r.getDataOffset(key), value);
		} else {
			Page<K> next = r.next(key);
			put(next, key, value);
			if (next.isOverflowed()) {
				Page<K> tmp = next.split();

				r.enter(tmp);
				r.close();
			}
		}
	}

	public Iterable<K> keys() throws Exception {
		Queue<K> queue = new LinkedList<>();
		collect(root, queue);
		return queue;
	}

	private void collect(Page<K> r, Queue<K> queue) throws Exception {
		if (r.isExternal()) {
			for (K key : r.asSymbolTable().keys())
				queue.add(key);
		} else {
			for (K key : r.asSymbolTable().keys())
				collect(r.next(key), queue);
		}
	}

	public int size() {
		return pageSerializer.getNumberOfKeys();
	}

}
