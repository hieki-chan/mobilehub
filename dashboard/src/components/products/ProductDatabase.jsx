import React, { useState, useEffect } from 'react';
import { motion } from 'framer-motion';
import { Edit, Search, Trash2, X, ChevronLeft, ChevronRight, UserPlus, LayoutGrid, Table } from 'lucide-react';
import { showPopupConfirm } from '../common_components/PopupConfirm';
import { fetchAdminProducts, deleteAdminProduct } from '../../api/ProductApi';


const ProductDatabase = ({ onAddClick, onEditClick, reloadFlag }) => {
    const [searchTerm, setSearchTerm] = useState("");
    const [filteredProducts, setFilteredProducts] = useState([]);
    const [newProduct, setNewProduct] = useState({ name: "", category: "", price: "", stock: "", sales: "" });
    const [currentPage, setCurrentPage] = useState(1);
    const itemsPerPage = 12;
    const [viewMode, setViewMode] = useState('table') // <— thêm chế độ hiển thị
    const [selectedProducts, setSelectedProducts] = useState([]);   // <— thêm trạng thái sản phẩm đã chọn
    const [sortConfig, setSortConfig] = useState({ key: null, direction: 'asc' });  // <— thêm trạng thái sắp xếp

    const [isDialogOpen, setDialogOpen] = useState(false);
    const [productToDelete, setProductToDelete] = useState(null);


    useEffect(() => {
        fetchAdminProducts(0, 10)
            .then((data) => {
                console.log("📦 Received from API:", data);

                if (data && data.content) {
                    const normalized = data.content.map(item => ({
                        id: item.id ?? 0,
                        name: item.name ?? "Chưa có tên",
                        category: item.category ?? "Không xác định",
                        price: item.price ?? 0,
                        stock: item.stock ?? 0,
                        sales: item.discountInPercent ?? 0,
                        status: item.status ?? "Không xác định",
                        imageUrl: item.imageUrl && item.imageUrl.trim() !== ""
                            ? item.imageUrl
                            : "https://via.placeholder.com/100x100?text=No+Image"
                    }));

                    setFilteredProducts(normalized);
                    console.table(normalized);
                }
            })
            .catch((err) => {
                console.error("🚨 Failed to load products:", err);
            });
    }, [reloadFlag]);


    const totalPages = Math.ceil(filteredProducts.length / itemsPerPage);

    const SearchHandler = (e) => {
        const term = e.target.value.toLowerCase();
        setSearchTerm(term);
        setFilteredProducts(prev =>
            prev.filter(product =>
                product.name.toLowerCase().includes(term) ||
                product.category.toLowerCase().includes(term)
            )
        );
        setCurrentPage(1);
    };

    const handleSort = (key) => {
        let direction = 'asc';
        if (sortConfig.key === key && sortConfig.direction === 'asc') {
            direction = 'desc';
        }

        const sorted = [...filteredProducts].sort((a, b) => {
            if (a[key] < b[key]) return direction === 'asc' ? -1 : 1;
            if (a[key] > b[key]) return direction === 'asc' ? 1 : -1;
            return 0;
        });

        setFilteredProducts(sorted);
        setSortConfig({ key, direction });
    };

    const handleEdit = (product) => {
        onEditClick(product);
    };

    const handleDelete = async (productId) => {
        const confirmed = await showPopupConfirm(
            "Xác nhận xoá sản phẩm",
            `Bạn có chắc chắn muốn xoá "${product.name}" không?`
        );

        if (!confirmed) return;

        try {
            await deleteAdminProduct(productId);
            alert("🗑️ Xoá sản phẩm thành công!");
            setFilteredProducts((prev) => prev.filter((p) => p.id !== productId)); // xoá trong UI luôn
        } catch (err) {
            console.error("❌ Lỗi xoá sản phẩm:", err);
            alert("Xoá thất bại!");
        }
    };

    const handleAdd = () => {
        const newId = filteredProducts.length > 0 ? Math.max(...filteredProducts.map(product => product.id)) + 1 : 1;
        const productToAdd = { ...newProduct, id: newId, price: parseFloat(newProduct.price), stock: parseInt(newProduct.stock), sales: parseInt(newProduct.sales) };
        setFilteredProducts([productToAdd, ...filteredProducts]);
        setAddModalOpen(false);
        setNewProduct({ name: "", category: "", price: "", stock: "", sales: "" }); // Reset new product state
    };


    const handleSave = () => {
        const updatedProducts = filteredProducts.map(product =>
            product.id === editProduct.id ? editProduct : product
        );
        setFilteredProducts(updatedProducts);
        setEditModalOpen(false);
    };


    const paginate = (pageNumber) => setCurrentPage(pageNumber);
    const getCurrentPageProducts = () => {
        const start = (currentPage - 1) * itemsPerPage;
        return filteredProducts.slice(start, start + itemsPerPage);
    };

    return (
        <motion.div
            className='bg-gray-800 bg-opacity-50 shadow-lg backdrop-blur-md rounded-xl p-5 border border-gray-700 mb-6 relative z-10'
            initial={{ opacity: 0, y: 25 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.2, delay: 0.2 }}
        >
            {/* ======================= Header and Search ==================*/}
            <div className='flex justify-between items-center mb-6'>
                <h2 className='text-xl font-semibold text-gray-100'>Danh sách sản phẩm</h2>
            </div>

            {/* ====================== Controls ===============================*/}
            <div className='flex justify-between items-center mb-6'>
                <button
                    onClick={() => setViewMode(viewMode === 'table' ? 'grid' : 'table')}
                    className='flex items-center gap-2 bg-gray-700 hover:bg-gray-600 text-white px-3 py-2 rounded-lg transition'
                >
                    {viewMode !== 'table' ? <LayoutGrid size={18} /> : <Table size={18} />}
                    {viewMode === 'table' ? '' : ''}
                </button>

                <div className='relative flex items-center'>
                    <Search className='absolute left-3 text-gray-400 sm:left-2.5 top-2.5' size={20} />
                    <input
                        type="text"
                        placeholder='Tìm kiếm sản phẩm...'
                        className='bg-gray-700 text-white placeholder-gray-400 rounded-lg pl-10 pr-4 py-2 w-full sm:w-auto focus:outline-none focus:ring-2 focus:ring-blue-500'
                        onChange={SearchHandler}
                        value={searchTerm}
                    />
                </div>

                <button
                    onClick={onAddClick}
                    className='flex items-center gap-2 bg-blue-600 hover:bg-blue-800 text-white px-4 py-2 rounded-lg transition'
                >
                    <UserPlus size={20} /> Thêm sản phẩm
                </button>
            </div>

            {/* ========== VIEW SWITCH ========== */}
            {viewMode === 'table' ? (
                /*================================== TABLE MODE ==============================*/
                <div className='overflow-x-auto'>
                    <table className='min-w-full divide-y divide-gray-400'>
                        <thead>
                            <tr>
                                {/* Checkbox chọn tất cả */}
                                <th className='w-12 py-3'>
                                    <div className='flex justify-center'>
                                        <input
                                            type="checkbox"
                                            checked={selectedProducts.length === filteredProducts.length}
                                            onChange={(e) => {
                                                if (e.target.checked) {
                                                    setSelectedProducts(filteredProducts.map(p => p.id))
                                                } else {
                                                    setSelectedProducts([])
                                                }
                                            }}
                                            className="accent-blue-500 w-4 h-4 cursor-pointer"
                                        />
                                    </div>
                                </th>

                                {/* Các tiêu đề có thể sắp xếp */}
                                {[
                                    { key: 'id', label: 'Mã' },
                                    { key: 'name', label: 'Sản phẩm' },
                                    { key: 'category', label: 'Danh mục' },
                                    { key: 'price', label: 'Giá tiền' },
                                    { key: 'stock', label: 'Kho' },
                                    { key: 'sales', label: 'Khuyến mãi' },
                                    { key: 'status', label: 'Trạng thái' },
                                ].map(({ key, label }) => (
                                    <th
                                        key={key}
                                        onClick={() => handleSort(key)}
                                        className='px-6 py-3 text-left text-sm font-medium text-gray-300 uppercase tracking-wider cursor-pointer select-none hover:text-blue-400'
                                    >
                                        {label}
                                        {sortConfig.key === key && (
                                            <span className="ml-1 text-blue-400">
                                                {sortConfig.direction === 'asc' ? '▲' : '▼'}
                                            </span>
                                        )}
                                    </th>
                                ))}

                                <th className='px-6 py-3 text-left text-sm font-medium text-gray-300 uppercase tracking-wider'></th>
                            </tr>
                        </thead>

                        <tbody className='divide-y divide-gray-500'>
                            {getCurrentPageProducts().map((product) => (
                                <motion.tr
                                    key={product.id}
                                    initial={{ opacity: 0 }}
                                    animate={{ opacity: 1 }}
                                    transition={{ duration: 0.3 }}
                                    className='odd:bg-gray-800 even:bg-gray-700 hover:bg-gray-600 transition-colors'
                                >
                                    {/* Checkbox từng dòng */}
                                    <td className='px-4 py-4'>
                                        <input
                                            type="checkbox"
                                            checked={selectedProducts.includes(product.id)}
                                            onChange={(e) => {
                                                if (e.target.checked) {
                                                    setSelectedProducts([...selectedProducts, product.id]);
                                                } else {
                                                    setSelectedProducts(selectedProducts.filter(id => id !== product.id));
                                                }
                                            }}
                                            className="accent-blue-500 w-4 h-4 cursor-pointer"
                                        />
                                    </td>

                                    <td className='px-6 py-4 whitespace-nowrap text-sm text-gray-100'>{product.id}</td>
                                    <td className='px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-100 flex gap-2 items-center'>
                                        <img
                                            src={product.imageUrl}
                                            alt="Product_Image"
                                            className='rounded-full size-10'
                                        />
                                        {product.name}
                                    </td>
                                    <td className='px-6 py-4 whitespace-nowrap text-sm text-gray-100'>{product.category}</td>
                                    <td className='px-6 py-4 whitespace-nowrap text-sm text-gray-100'>{product.price.toLocaleString('vi-VN')}₫</td>
                                    <td className='px-6 py-4 whitespace-nowrap text-sm text-gray-100'>{product.stock}</td>
                                    <td className='px-6 py-4 whitespace-nowrap text-sm text-gray-100'>{product.sales}</td>
                                    <td className='px-6 py-4 whitespace-nowrap text-sm text-gray-100'>
                                        <span
                                            className={`px-3 py-1 rounded-full text-xs font-semibold 
                                            ${product.status === "ACTIVE"
                                                        ? "bg-green-700 text-green-100"
                                                        : "bg-red-700 text-red-100"
                                                    }`}
                                            >
                                            {product.status == 'ACTIVE' ? 'Hoạt động' : 'Ngừng hoạt động'}
                                        </span>
                                    </td>
                                    <td className='px-6 py-4 whitespace-nowrap text-sm font-medium h-full'>
                                        <div className='flex items-center gap-4 h-full'>
                                            <button onClick={() => handleEdit(product)} className='text-blue-500 hover:text-blue-700'>
                                                <Edit size={18} />
                                            </button>
                                            <button onClick={() => handleDelete(product.id)} className='text-red-500 hover:text-red-700'>
                                                <Trash2 size={18} />
                                            </button>
                                        </div>
                                    </td>
                                </motion.tr>
                            ))}
                        </tbody>
                    </table>
                </div>

            ) : (
                /* ================================= GRID MODE =================================== */
                <motion.div
                    className='grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 xl:grid-cols-4 gap-6 w-full'
                    initial={{ opacity: 0 }}
                    animate={{ opacity: 1 }}
                    transition={{ duration: 0.2 }}
                >
                    {getCurrentPageProducts().map((product) => (
                        <motion.div
                            key={product.id}
                            className='w-full max-w-xs bg-gray-700 rounded-xl p-4 flex flex-col items-center shadow-lg hover:shadow-2xl transition mx-auto'
                            whileHover={{ scale: 1.05 }}
                        >
                            <img
                                src={product.imageUrl}
                                alt={product.name}
                                className='w-45 h-64 object-cover rounded-lg mb-3'
                            />
                            <p className='text-gray-400 text-xs mb-1'>#{product.id}</p>
                            <h3 className='text-lg font-semibold text-gray-100'>{product.name}</h3>
                            <p className='text-gray-400 text-sm'>{product.category}</p>
                            <p className='text-blue-400 text-md font-bold mt-2'>{product.price.toLocaleString('vi-VN')}₫</p>
                            <p className='text-sm text-gray-400 mt-1'>Kho: {product.stock}</p>

                            <div className='flex gap-3 mt-3'>
                                <button onClick={() => handleEdit(product)} className='text-blue-400 hover:text-blue-600'>
                                    <Edit size={18} />
                                </button>
                                <button onClick={() => handleDelete(product.id)} className='text-red-400 hover:text-red-600'>
                                    <Trash2 size={18} />
                                </button>
                            </div>
                        </motion.div>
                    ))}
                </motion.div>
            )}

            {/* Enhanced Pagination Controls */}
            <div className='flex flex-col md:flex-row justify-between mt-4 space-x-2 items-center'>
                <div className='flex items-center'>
                    <button
                        onClick={() => paginate(currentPage - 1)}
                        disabled={currentPage === 1}
                        className={`text-sm px-3 py-1 border rounded-md ${currentPage === 1 ? 'text-gray-400 border-gray-600' : 'text-gray-100 border-gray-300 hover:bg-gray-300 hover:text-gray-800'}`}
                    >
                        <ChevronLeft size={18} />
                    </button>
                    <span className='mx-2 text-sm font-medium text-gray-100'>Trang {currentPage} / {totalPages}</span>
                    <button
                        onClick={() => paginate(currentPage + 1)}
                        disabled={currentPage === totalPages}
                        className={`text-sm px-3 py-1 border rounded-md ${currentPage === totalPages ? 'text-gray-400 border-gray-600' : 'text-gray-100 border-gray-300 hover:bg-gray-300 hover:text-gray-800'}`}
                    >
                        <ChevronRight size={18} />
                    </button>
                </div>

                <div className='text-sm font-medium text-gray-300 tracking-wider mt-5 md:mt-0'>Tổng sản phẩm: {filteredProducts.length}</div>
            </div>
        </motion.div>
    );
};

export default ProductDatabase;
