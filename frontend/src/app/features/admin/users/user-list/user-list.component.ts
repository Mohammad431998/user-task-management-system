import { CommonModule } from '@angular/common';
import { Component, inject, OnInit } from '@angular/core';
import { FormBuilder, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';
import { UserService } from '../../../../core/services/user.service';
import { User, UserStatus } from '../../../../core/models/user.model';
import { Role } from '../../../../core/models/auth.model';

/** Requires at least 8 chars with an uppercase, lowercase, number and symbol. */
const STRONG_PASSWORD_PATTERN =
  /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[^A-Za-z0-9]).{8,}$/;

@Component({
  selector: 'app-user-list',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, FormsModule],
  templateUrl: './user-list.component.html',
  styleUrl: './user-list.component.scss'
})
export class UserListComponent implements OnInit {
  private userService = inject(UserService);
  private fb = inject(FormBuilder);

  users: User[] = [];
  roles: Role[] = [];
  loading = true;

  searchTerm = '';
  roleFilter = '';
  statusFilter = '';

  showModal = false;
  editingUser: User | null = null;
  saving = false;
  errorMessage = '';

  form = this.fb.group({
    username: ['', Validators.required],
    name: ['', Validators.required],
    email: ['', [Validators.required, Validators.email]],
    password: [''],
    roleId: [null as number | null, Validators.required]
  });

  ngOnInit(): void {
    this.userService.getRoles().subscribe((roles) => (this.roles = roles));
    this.loadUsers();
  }

  loadUsers(): void {
    this.loading = true;
    this.userService
      .search(
        this.searchTerm || undefined,
        this.roleFilter ? Number(this.roleFilter) : undefined,
        (this.statusFilter as UserStatus) || undefined
      )
      .subscribe((users) => {
        this.users = users;
        this.loading = false;
      });
  }

  onFilterChange(): void {
    this.loadUsers();
  }

  openCreateModal(): void {
    this.editingUser = null;
    this.errorMessage = '';
    this.form.reset({ username: '', name: '', email: '', password: '', roleId: null });
    this.form
      .get('password')
      ?.addValidators([Validators.required, Validators.pattern(STRONG_PASSWORD_PATTERN)]);
    this.form.get('password')?.updateValueAndValidity();
    this.showModal = true;
  }

  openEditModal(user: User): void {
    this.editingUser = user;
    this.errorMessage = '';
    this.form.reset({
      username: user.username ?? '',
      name: user.name,
      email: user.email,
      password: '',
      roleId: user.roleId
    });
    this.form.get('password')?.clearValidators();
    this.form.get('password')?.addValidators(Validators.pattern(STRONG_PASSWORD_PATTERN));
    this.form.get('password')?.updateValueAndValidity();
    this.showModal = true;
  }

  closeModal(): void {
    this.showModal = false;
  }

  save(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.saving = true;
    this.errorMessage = '';
    const value = this.form.getRawValue();
    const payload = {
      username: value.username!,
      name: value.name!,
      email: value.email!,
      password: value.password || '',
      roleId: value.roleId!
    };

    const request$ = this.editingUser
      ? this.userService.update(this.editingUser.id, payload)
      : this.userService.create(payload);

    request$.subscribe({
      next: () => {
        this.saving = false;
        this.showModal = false;
        this.loadUsers();
      },
      error: (err: HttpErrorResponse) => {
        this.saving = false;
        this.errorMessage = err.error?.message || 'Failed to save user.';
      }
    });
  }

  toggleStatus(user: User): void {
    const newStatus: UserStatus = user.status === 'ACTIVE' ? 'INACTIVE' : 'ACTIVE';
    this.userService.updateStatus(user.id, { status: newStatus }).subscribe(() => this.loadUsers());
  }

  deleteUser(user: User): void {
    if (!confirm(`Delete user "${user.name}"? This action cannot be undone.`)) return;
    this.userService.delete(user.id).subscribe(() => this.loadUsers());
  }
}
