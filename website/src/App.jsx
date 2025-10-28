import React from 'react'
import { Routes, Route } from 'react-router-dom'
import Header from './components/Header'
import Footer from './components/Footer'
import Home from './pages/Home'
import Login from './pages/Login'
import Register from './pages/Register'
import ProductDetail from './pages/ProductDetail'
import Cart from './pages/Cart'
import SearchResults from './pages/SearchResults'
import Profile from './pages/Profile'

export default function App(){
  const hideFooterPaths = ['/product/','/cart','/search']
  const currentPath = window.location.pathname
  const hideFooter = hideFooterPaths.some(p => currentPath.startsWith(p))
  return (
    <>
      <Header />
      <Routes>
        <Route path="/" element={<Home/>} />
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />
        <Route path="/product/:id" element={<ProductDetail/>} />
        <Route path="/cart" element={<Cart />} />
        <Route path="/search" element={<SearchResults />} /> 
        <Route path='/profile' element={<Profile />} />
      </Routes>
      {!hideFooter && <Footer />}
    </>
  )
}
