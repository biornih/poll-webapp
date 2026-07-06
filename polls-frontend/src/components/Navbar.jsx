import { Link, useNavigate } from 'react-router-dom'
import { api } from '../api/api.js'

function Navbar() {
    const navigate = useNavigate()

    function handleLogout() {
        api.logout()
        navigate('/login')
    }

    return (
        <nav className="navbar navbar-expand-lg navbar-dark bg-purple">
            <div className="container">
                <Link className="navbar-brand" to="/polls">Poll Manager</Link>
                <div className="d-flex gap-2">
                    <Link className="btn btn-outline-light btn-sm" to="/polls">
                        My Polls
                    </Link>
                    <Link className="btn btn-outline-light btn-sm" to="/polls/pending">
                        Pending
                    </Link>
                    <Link className="btn btn-outline-light btn-sm" to="/polls/create">
                        Create Poll
                    </Link>
                    <button
                        className="btn btn-light btn-sm"
                        onClick={handleLogout}
                    >
                        Logout
                    </button>
                </div>
            </div>
        </nav>
    )
}

export default Navbar