import { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { api } from '../api/api.js'
import Navbar from '../components/Navbar.jsx'
import { Link } from 'react-router-dom'

function ProfilePage() {
    const [stats, setStats] = useState(null)
    const [error, setError] = useState(null)
    const [newUsername, setNewUsername] = useState('')
    const [currentPassword, setCurrentPassword] = useState('')
    const [newPassword, setNewPassword] = useState('')
    const [editError, setEditError] = useState(null)
    const [editSuccess, setEditSuccess] = useState(null)
    const [editLoading, setEditLoading] = useState(false)
    const navigate = useNavigate()
    const [editOpen, setEditOpen] = useState(false)

    useEffect(() => {
        if (!api.isLoggedIn()) {
            navigate('/login')
            return
        }
        loadStats()
    }, [])

    async function loadStats() {
        try {
            const data = await api.getStats()
            setStats(data)
        } catch (err) {
            setError(err.message)
        }
    }

    async function handleUpdateUsername(e) {
        e.preventDefault()
        setEditError(null)
        setEditSuccess(null)
        setEditLoading(true)
        try {
            await api.updateUsername(newUsername)
            localStorage.setItem('username', newUsername)
            setEditSuccess('Username updated successfully')
            setNewUsername('')
            loadStats()
        } catch (err) {
            setEditError(err.message)
        } finally {
            setEditLoading(false)
        }
    }

    async function handleUpdatePassword(e) {
        e.preventDefault()
        setEditError(null)
        setEditSuccess(null)
        setEditLoading(true)
        try {
            await api.updatePassword(currentPassword, newPassword)
            localStorage.setItem('password', newPassword)
            setEditSuccess('Password updated successfully')
            setCurrentPassword('')
            setNewPassword('')
        } catch (err) {
            setEditError(err.message)
        } finally {
            setEditLoading(false)
        }
    }

    if (!stats) {
        return (
            <div>
                <Navbar />
                <div className="container mt-4">
                    <div className="text-center">Loading...</div>
                </div>
            </div>
        )
    }

    return (
        <div>
            <Navbar />
            <div className="container mt-4">
                <div className="row justify-content-center">
                    <div className="col-md-6">
                        <div className="card shadow text-center">
                            <div className="card-body p-5">
                                <div className={`avatar-ring ${stats.pendingPolls > 0 ? 'has-pending' : ''} mb-3`}>
                                    <div
                                        className={`rounded-circle d-inline-flex align-items-center
                      justify-content-center ${stats.pendingPolls > 0 ? 'avatar-pulse' : ''}`}
                                        style={{
                                            width: 80,
                                            height: 80,
                                            background: 'linear-gradient(135deg, #6f42c1, #b39ddb)',
                                            fontSize: 32,
                                            color: 'white'
                                        }}
                                    >
                                        {stats.username.charAt(0).toUpperCase()}
                                    </div>
                                </div>
                                <h3 className="mb-1">{stats.username}</h3>
                                {stats.pendingPolls > 0 && (
                                        <p className="text-danger mb-3">
                                            You have {stats.pendingPolls} pending poll
                                            {stats.pendingPolls > 1 ? 's' : ''}!
                                        </p>
                                    )}
                                {stats.pendingPolls === 0 && (
                                    <p className="text-muted mb-3">No pending polls</p>
                                )}
                                {error && (
                                    <div className="alert alert-danger">{error}</div>
                                )}
                                <div className="row g-3 mt-2">
                                    <div className="col-4">
                                        <Link to="/polls" className="text-decoration-none">
                                            <div className="card bg-light h-100">
                                                <div className="card-body py-3">
                                                    <div className="fs-2 fw-bold text-purple">
                                                        {stats.pollsCreated}
                                                    </div>
                                                    <small className="text-muted">Polls Created</small>
                                                </div>
                                            </div>
                                        </Link>
                                    </div>
                                    <div className="col-4">
                                        <Link to="/polls/pending" className="text-decoration-none">
                                            <div className="card bg-light h-100">
                                                <div className="card-body py-3">
                                                    <div className="fs-2 fw-bold text-danger">
                                                        {stats.pendingPolls}
                                                    </div>
                                                    <small className="text-muted">Pending</small>
                                                </div>
                                            </div>
                                        </Link>
                                    </div>
                                    <div className="col-4">
                                        <div className="card bg-light h-100">
                                            <div className="card-body py-3">
                                                <div className="fs-2 fw-bold text-success">
                                                    {stats.pollsAnswered}
                                                </div>
                                                <small className="text-muted">Answered</small>
                                            </div>
                                        </div>
                                    </div>
                                </div>

                                <button
                                    className="btn btn-outline-purple mt-3"
                                    onClick={() => setEditOpen(true)}
                                >
                                    Edit Profile
                                </button>

                            </div>
                        </div>
                    </div>
                </div>
            </div>
            {editOpen && (
                <div
                    className="modal d-block"
                    style={{ backgroundColor: 'rgba(0,0,0,0.5)' }}
                    onClick={e => { if (e.target === e.currentTarget) setEditOpen(false) }}
                >
                    <div className="modal-dialog modal-dialog-centered">
                        <div className="modal-content">
                            <div className="modal-header">
                                <h5 className="modal-title">Edit Profile</h5>
                                <button
                                    className="btn-close"
                                    onClick={() => setEditOpen(false)}
                                />
                            </div>
                            <div className="modal-body">
                                {editError && (
                                    <div className="alert alert-danger py-2">{editError}</div>
                                )}
                                {editSuccess && (
                                    <div className="alert alert-success py-2">{editSuccess}</div>
                                )}
                                <form onSubmit={handleUpdateUsername} className="mb-4">
                                    <label className="form-label fw-bold">Change Username</label>
                                    <div className="d-flex gap-2 align-items-center">
                                        <input
                                            type="text"
                                            className="form-control"
                                            placeholder="New username"
                                            value={newUsername}
                                            onChange={e => setNewUsername(e.target.value)}
                                            required
                                        />
                                        <button
                                            type="submit"
                                            className="btn btn-purple text-center"
                                            disabled={editLoading}
                                            style={{ minWidth: 90, flexShrink: 0 }}
                                        >
                                            Update
                                        </button>
                                    </div>
                                </form>
                                <form onSubmit={handleUpdatePassword}>
                                    <label className="form-label fw-bold">Change Password</label>
                                    <input
                                        type="password"
                                        className="form-control mb-2"
                                        placeholder="Current password"
                                        value={currentPassword}
                                        onChange={e => setCurrentPassword(e.target.value)}
                                        required
                                    />
                                    <input
                                        type="password"
                                        className="form-control mb-2"
                                        placeholder="New password"
                                        value={newPassword}
                                        onChange={e => setNewPassword(e.target.value)}
                                        required
                                    />
                                    <button
                                        type="submit"
                                        className="btn btn-purple"
                                        disabled={editLoading}
                                    >
                                        {editLoading ? 'Updating...' : 'Change Password'}
                                    </button>
                                    <small className="text-muted d-block mt-1">
                                        Password can only be changed once per week
                                    </small>
                                </form>
                            </div>
                            <div className="modal-footer">
                                <button
                                    className="btn btn-secondary"
                                    onClick={() => setEditOpen(false)}
                                >
                                    Close
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
            )}
        </div>
    )
}

export default ProfilePage