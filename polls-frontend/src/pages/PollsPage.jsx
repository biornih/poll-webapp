import { useState, useEffect } from 'react'
import { useNavigate, Link } from 'react-router-dom'
import { api } from '../api/api.js'
import Navbar from '../components/Navbar.jsx'

function PollsPage() {
    const [polls, setPolls] = useState([])
    const [error, setError] = useState(null)
    const [inviteUsername, setInviteUsername] = useState('')
    const [invitePollId, setInvitePollId] = useState(null)
    const [inviteError, setInviteError] = useState(null)
    const [inviteSuccess, setInviteSuccess] = useState(null)
    const navigate = useNavigate()
    const [recentInvites, setRecentInvites] = useState({})
    const [lastInvitedUsername, setLastInvitedUsername] = useState('')

    useEffect(() => {
        if (!api.isLoggedIn()) {
            navigate('/login')
            return
        }
        loadPolls()
    }, [])

    useEffect(() => {
        const timer = setInterval(() => {
            setRecentInvites(prev => {
                const updated = {}
                let hasAny = false
                for (const key in prev) {
                    if (prev[key] > 0) {
                        updated[key] = prev[key] - 1
                        hasAny = true
                    }
                }
                return hasAny ? updated : {}
            })
        }, 1000)
        return () => clearInterval(timer)
    }, [])

    async function loadPolls() {
        try {
            const data = await api.listPolls()
            setPolls(data)
        } catch (err) {
            setError(err.message)
        }
    }

    async function handleDelete(id) {
        if (!window.confirm('Delete this poll?')) return
        try {
            await api.deletePoll(id)
            setPolls(polls.filter(p => p.id !== id))
        } catch (err) {
            setError(err.message)
        }
    }

    async function handleFinish(id) {
        if (!window.confirm('Finish this poll? This cannot be undone.')) return
        try {
            const updated = await api.finishPoll(id)
            setPolls(polls.map(p => p.id === id ? updated : p))
        } catch (err) {
            setError(err.message)
        }
    }

    async function handleInvite(e) {
        e.preventDefault()
        setInviteError(null)
        setInviteSuccess(null)
        try {
            await api.inviteUser(invitePollId, inviteUsername)
            setInviteSuccess(`${inviteUsername} invited successfully`)
            setLastInvitedUsername(inviteUsername)
            const key = `${invitePollId}-${inviteUsername}`
            setRecentInvites(prev => ({ ...prev, [key]: 10 }))
            setInviteUsername('')
        } catch (err) {
            setInviteError(err.message)
        }
    }

    /*async function handleUninvite(pollId, username) {
        try {
            await api.uninvite(pollId, username)
            const key = `${pollId}-${username}`
            setRecentInvites(prev => {
                const updated = { ...prev }
                delete updated[key]
                return updated
            })
            setInviteSuccess(null)
        } catch (err) {
            setInviteError(err.message)
        }
    }*/

    async function handleUninvite(pollId, username) {
        console.log('uninvite called with:', pollId, username)
        try {
            await api.uninvite(pollId, username)
            const key = `${pollId}-${username}`
            setRecentInvites(prev => {
                const updated = { ...prev }
                delete updated[key]
                return updated
            })
            setInviteSuccess(null)
        } catch (err) {
            setInviteError(err.message)
        }
    }

    return (
        <div>
            <Navbar />

            <div className="container mt-4">
                <div className="d-flex justify-content-between align-items-center mb-4">
                    <h2>My Polls</h2>
                    <div className="d-flex gap-2">
                        <button
                            className="btn btn-outline-secondary btn-sm"
                            onClick={() => window.print()}
                        >
                            🖨️ Print
                        </button>
                        <Link to="/polls/create" className="btn btn-purple">
                            + Create Poll
                        </Link>
                    </div>
                </div>

                {error && <div className="alert alert-danger">{error}</div>}

                {polls.length === 0 && (
                    <div className="alert alert-info">
                        You haven't created any polls yet.
                    </div>
                )}

                {polls.map(poll => (
                    <div key={poll.id} className="card mb-3 shadow-sm">
                        <div className="card-body">
                            <div className="d-flex justify-content-between align-items-start">
                                <div>
                                    <h5 className="card-title">{poll.title}</h5>
                                    <p className="card-text text-muted">{poll.description}</p>
                                    <small className="text-muted">
                                        Due: {new Date(poll.dueDate).toLocaleDateString('de-DE')}                                        {poll.questions.length} questions &nbsp;|&nbsp;
                                        <span className={
                                            poll.finished ? 'text-danger' :
                                                new Date(poll.dueDate) < new Date() ? 'text-warning' :
                                                    'text-success'
                                        }>
                                          {poll.finished ? 'Finished' :
                                              new Date(poll.dueDate) < new Date() ? 'Expired' :
                                                  'Active'}
                                        </span>
                                    </small>
                                </div>
                                <div className="d-flex gap-2 flex-wrap justify-content-end">
                                    {!poll.finished && (
                                        <>
                                            {new Date(poll.dueDate) >= new Date() && (
                                                <button
                                                    className="btn btn-sm btn-outline-purple"
                                                    onClick={() => {
                                                        setInvitePollId(poll.id)
                                                        setInviteError(null)
                                                        setInviteSuccess(null)
                                                        setInviteUsername('')
                                                    }}
                                                >
                                                    Invite
                                                </button>
                                            )}
                                            <button
                                                className="btn btn-sm btn-warning"
                                                onClick={() => handleFinish(poll.id)}
                                            >
                                                Finish
                                            </button>
                                        </>
                                    )}
                                    {poll.finished && (
                                        <Link
                                            to={`/polls/${poll.id}/results`}
                                            className="btn btn-sm btn-info"
                                        >
                                            Results
                                        </Link>
                                    )}
                                    <button
                                        className="btn btn-sm btn-danger"
                                        onClick={() => handleDelete(poll.id)}
                                    >
                                        Delete
                                    </button>
                                </div>
                            </div>

                            {invitePollId === poll.id && (
                                <div className="mt-3 border-top pt-3">
                                    <form onSubmit={handleInvite} className="d-flex gap-2">
                                        <input
                                            type="text"
                                            className="form-control form-control-sm"
                                            placeholder="Username to invite"
                                            value={inviteUsername}
                                            onChange={e => setInviteUsername(e.target.value)}
                                            required
                                        />
                                        <button
                                            type="submit"
                                            className="btn btn-sm btn-purple"
                                            style={{ flexShrink: 0 }}
                                        >
                                            Send
                                        </button>
                                        <button
                                            type="button"
                                            className="btn btn-sm btn-secondary"
                                            style={{ flexShrink: 0 }}
                                            onClick={() => setInvitePollId(null)}
                                        >
                                            Cancel
                                        </button>
                                    </form>
                                    {inviteError && (
                                        <div className="alert alert-danger mt-2 py-1">
                                            {inviteError}
                                        </div>
                                    )}
                                    {inviteSuccess && (
                                        <div className="d-flex align-items-center gap-2 mt-2">
                                            <div className="alert alert-success py-1 mb-0 flex-grow-1">
                                                {inviteSuccess}
                                            </div>
                                            {recentInvites[`${invitePollId}-${lastInvitedUsername}`] > 0 && (
                                                <button
                                                    type="button"
                                                    className="btn btn-sm btn-outline-danger"
                                                    onClick={() => handleUninvite(invitePollId, lastInvitedUsername)}
                                                >
                                                    Undo ({recentInvites[`${invitePollId}-${lastInvitedUsername}`]}s)
                                                </button>
                                            )}
                                        </div>
                                    )}
                                </div>
                            )}
                        </div>
                    </div>
                ))}
            </div>
        </div>
    )
}

export default PollsPage